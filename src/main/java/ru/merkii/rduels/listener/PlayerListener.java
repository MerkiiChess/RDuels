package ru.merkii.rduels.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.statistic.StatisticService;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.UUID;

@Singleton
public class PlayerListener implements Listener {

    private final RDuels plugin;
    private final StatisticService statisticService;
    private final SettingsConfiguration settings;

    @Inject
    public PlayerListener(RDuels plugin, StatisticService statisticService, SettingsConfiguration settings) {
        this.plugin = plugin;
        this.statisticService = statisticService;
        this.settings = settings;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        // Recover from a crash mid-fight: kits may have altered the max-health attribute.
        PlayerUtil.resetMaxHealth(player);
        if (this.settings.itemOpenCustomKit()) {
            player.getInventory().setItem(this.settings.createCustomKit().slot(), this.settings.createCustomKit().build());
        }
        // Warm the cache asynchronously, then apply the stored day/night preference back on
        // the main thread once it is available (never blocking the join).
        this.statisticService.load(uuid)
                .thenRun(() -> Bukkit.getScheduler().runTask(this.plugin, () -> applyTimePreference(player)));
    }

    private void applyTimePreference(Player player) {
        if (!player.isOnline()) {
            return;
        }
        UUID uuid = player.getUniqueId();
        if (this.statisticService.isDay(uuid)) {
            player.setPlayerTime(this.settings.dayTicks(), false);
        } else if (this.statisticService.isNight(uuid)) {
            player.setPlayerTime(this.settings.nightTicks(), false);
        }
    }

    // MONITOR so that any stat changes made by other quit handlers (e.g. a duel ending on
    // disconnect) are already applied to the cache before we persist and unload it.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.statisticService.flushAndUnload(event.getPlayer().getUniqueId());
    }

}

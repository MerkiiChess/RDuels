package ru.merkii.rduels.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.manager.DatabaseManager;

@Singleton
public class PlayerListener implements Listener {

    private final DatabaseManager databaseManager;
    private final SettingsConfiguration settings;

    @Inject
    public PlayerListener(DatabaseManager databaseManager, SettingsConfiguration settings) {
        this.databaseManager = databaseManager;
        this.settings = settings;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.settings.itemOpenCustomKit()) {
            player.getInventory().setItem(this.settings.createCustomKit().slot(), this.settings.createCustomKit().build());
        }
        if (databaseManager.isDay(player.getUniqueId()).join()) {
            player.setPlayerTime(this.settings.dayTicks(), false);
        } else if (databaseManager.isNight(player.getUniqueId()).join()) {
            player.setPlayerTime(this.settings.nightTicks(), false);
        }
    }

}

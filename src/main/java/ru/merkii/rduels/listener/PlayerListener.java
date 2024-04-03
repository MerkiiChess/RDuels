package ru.merkii.rduels.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.Settings;

public class PlayerListener implements Listener {

    private final RDuels plugin = RDuels.getInstance();
    private final Settings settings = plugin.getSettings();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.settings.isItemOpenCustomKit()) {
            player.getInventory().setItem(this.settings.getCreateCustomKit().getSlot(), this.settings.getCreateCustomKit().build());
        }
        if (this.plugin.getDatabaseManager().isDay(player).join()) {
            player.setPlayerTime(this.settings.getDayTicks(), false);
        } else if (this.plugin.getDatabaseManager().isNight(event.getPlayer()).join()) {
            player.setPlayerTime(this.settings.getNightTicks(), false);
        }
    }

}

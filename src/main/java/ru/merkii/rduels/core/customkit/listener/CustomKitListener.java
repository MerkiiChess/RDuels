package ru.merkii.rduels.core.customkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.Settings;
import ru.merkii.rduels.core.customkit.menu.CustomKitCreateMenu;
import ru.merkii.rduels.core.duel.DuelCore;

public class CustomKitListener implements Listener {

    private final RDuels plugin = RDuels.getInstance();
    private final Settings settings = plugin.getSettings();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (DuelCore.INSTANCE.getDuelAPI().isFightPlayer(player)) {
            return;
        }
        if (!player.getInventory().getItemInMainHand().equals(this.settings.getCreateCustomKit().build())) {
            return;
        }
        if (!this.settings.isItemOpenCustomKit()) {
            return;
        }
        event.setCancelled(true);
        new CustomKitCreateMenu(player).open(player);
    }

}
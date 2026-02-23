package ru.merkii.rduels.core.customkit.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.customkit.menu.CustomKitCreateMenu;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;

@Singleton
public class CustomKitListener implements Listener {

    private final DuelAPI duelAPI;
    private final SettingsConfiguration settings;

    @Inject
    public CustomKitListener(SettingsConfiguration settings, DuelAPI duelAPI) {
        this.duelAPI = duelAPI;
        this.settings = settings;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if (duelPlayer.isFight()) {
            return;
        }
        if (!player.getInventory().getItemInMainHand().equals(this.settings.createCustomKit().build())) {
            return;
        }
        if (!this.settings.itemOpenCustomKit()) {
            return;
        }
        event.setCancelled(true);
        new CustomKitCreateMenu().open(player);
    }

}
package ru.merkii.rduels.core.customkit.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CustomKitListener implements Listener {

    SettingsConfiguration settings;
    CustomKitStorage customKitStorage;
    InventoryGUIFactory factory;

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
        ItemStack expectedItem = this.settings.createCustomKit().build();
        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand == null || !inHand.isSimilar(expectedItem)) {
            return;
        }
        if (!this.settings.itemOpenCustomKit()) {
            return;
        }
        event.setCancelled(true);
        factory.create("create-kit", player, InventoryContext.empty()).ifPresent(InventoryGUI::open);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        customKitStorage.loadKits(BukkitAdapter.adapt(event.getPlayer()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        leave(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        leave(event.getPlayer());
    }

    private void leave(Player player) {
        customKitStorage.unload(player.getUniqueId());
    }

}

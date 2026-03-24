package ru.merkii.rduels.core.duel.listener;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.duel.matchmaking.DuelMatchmakingService;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
public class MatchmakingItemListener implements Listener {

    SettingsConfiguration settings;
    DuelMatchmakingService matchmakingService;
    InventoryGUIFactory factory;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ItemStack playerItem = event.getItem();
        if (playerItem == null || playerItem.getType().isAir()) {
            return;
        }
        if (playerItem.isSimilar(settings.matchmakingItem().build()))  {
            onMatchmakingItem(event.getPlayer());
            event.setCancelled(true);
            return;
        }
        if (playerItem.isSimilar(settings.matchmakingLeaveItem().build())) {
            onMatchmakingLeaveItem(event.getPlayer());
            event.setCancelled(true);
        }
    }

    private void onMatchmakingLeaveItem(Player player) {
        matchmakingService.leaveQueue(BukkitAdapter.adapt(player));
        PlayerInventory inventory = player.getInventory();
        inventory.setItem(settings.matchmakingItem().slot(), settings.matchmakingItem().build());
        inventory.setItem(settings.createCustomKit().slot(), settings.createCustomKit().build());
        player.updateInventory();
    }

    private void onMatchmakingItem(Player player) {
        InventoryContext context = InventoryContext.empty();
        context.extend("option_type", "kit");
        factory.create("duel-matchmaking", player, context)
                .ifPresent(InventoryGUI::open);
    }

}

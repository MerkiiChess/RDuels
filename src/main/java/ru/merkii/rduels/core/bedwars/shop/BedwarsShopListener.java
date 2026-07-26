package ru.merkii.rduels.core.bedwars.shop;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/** Routes clicks in the Bedwars shop/upgrade inventories to the shop service (read-only otherwise). */
@Singleton
public class BedwarsShopListener implements Listener {

    private final BedwarsShopService shopService;

    @Inject
    public BedwarsShopListener(BedwarsShopService shopService) {
        this.shopService = shopService;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BedwarsMenuHolder holder)) {
            return;
        }
        event.setCancelled(true);
        if (event.getClickedInventory() != event.getInventory() || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        shopService.handleClick(player, holder, event.getSlot());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof BedwarsMenuHolder) {
            event.setCancelled(true);
        }
    }
}

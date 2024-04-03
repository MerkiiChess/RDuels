package ru.merkii.rduels.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.PlayerInventory;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.menu.VMenu;
import ru.merkii.rduels.menu.event.ClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof VMenu)) {
            return;
        }
        event.setCancelled(true);
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
            return;
        }
        VMenu vMenu = (VMenu) player.getOpenInventory().getTopInventory().getHolder();
        vMenu.onClick(new ClickEvent(
                player,
                event.getRawSlot(),
                event.getClickedInventory() instanceof PlayerInventory,
                event.getCurrentItem(),
                vMenu.getItemBuilder(event.getRawSlot()),
                event.isShiftClick(),
                event.isRightClick(),
                event.isLeftClick()
        ));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof VMenu)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof VMenu)) {
            return;
        }
        VMenu vMenu = (VMenu) player.getOpenInventory().getTopInventory().getHolder();
        vMenu.onClose(event);
    }

}

package ru.merkii.rduels.listener;

import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.merkii.rduels.gui.internal.GUIHolder;
import ru.merkii.rduels.gui.internal.GUIItem;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

@Singleton
public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GUIHolder holder)) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        GUIItem guiItem = holder.getGuiItem(slot);
        if (guiItem == null) return;

        InventoryContext clickContext = holder.getBaseContext().copy();

        clickContext.overrideOrCreate("player", player);
        clickContext.overrideOrCreate("click_type", event.getClick());
        clickContext.overrideOrCreate("event", event);
        clickContext.overrideOrCreate("slot", slot);
        clickContext.overrideOrCreate("model", guiItem.model());
        clickContext.overrideOrCreate("item_config", guiItem.config());
        clickContext.overrideOrCreate("item", guiItem);

        guiItem.config().createHandler(clickContext).handle();
    }
}
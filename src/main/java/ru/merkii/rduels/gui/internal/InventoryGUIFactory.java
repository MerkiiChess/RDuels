package ru.merkii.rduels.gui.internal;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.Optional;

public interface InventoryGUIFactory {

    Optional<InventoryGUI> create(String guiName, Player player, InventoryContext context);
    ItemStack buildItemStack(Player player, InventoryItem itemConfig, InventoryContext context, Object model);

}

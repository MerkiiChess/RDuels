package ru.merkii.rduels.gui.provider;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.config.menu.settings.gui.GuiSettings;
import ru.merkii.rduels.gui.internal.GUIHolder;
import ru.merkii.rduels.gui.internal.GUIItem;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BaseInventoryGUI implements InventoryGUI {

    Player player;
    Inventory inventory;
    GUIHolder holder;
    InventoryContext context;

    public BaseInventoryGUI(Player player, GuiSettings settings, int size, InventoryContext context) {
        this.player = player;
        this.context = context;
        this.context.overrideOrCreate("gui", this);
        this.holder = new GUIHolder(this.context);
        this.inventory = Bukkit.createInventory(holder, size, settings.title());
        this.holder.setInventory(inventory);
    }

    public void setItem(int slot, GUIItem guiItem) {
        holder.setItem(slot, guiItem);
        inventory.setItem(slot, guiItem.itemStack());
    }

    public void updateAll(InventoryGUIFactory factory) {
        holder.getGuiItems().forEach((slot, guiItem) -> {
            ItemStack updatedStack = factory.buildItemStack(player, guiItem.config(), context, guiItem.model());
            inventory.setItem(slot, updatedStack);
        });
    }

    @Override
    public void open() {
        player.openInventory(inventory);
    }
}
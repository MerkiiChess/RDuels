package ru.merkii.rduels.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.menu.event.ClickEvent;
import ru.merkii.rduels.util.ColorUtil;
import java.util.HashMap;
import java.util.Map;

public abstract class VMenu implements InventoryHolder {

    private final Map<Integer, ItemBuilder> items = new HashMap<>();
    protected Inventory inventory;

    public VMenu(int size, String title) {
        inventory = Bukkit.createInventory(this, size, ColorUtil.color(title));
    }

    public VMenu() {
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    public void setItem(int slot, ItemBuilder itemBuilder) {
        this.inventory.setItem(slot, itemBuilder.build());
        this.items.put(slot, itemBuilder);
    }

    public void setItem(ItemBuilder itemBuilder) {
        this.setItem(itemBuilder.getSlot(), itemBuilder);
    }

    public ItemBuilder getItemBuilder(int slot) {
        if (this.items.isEmpty()) return null;
        return this.items.get(slot);
    }

    public abstract void onClick(ClickEvent event);

    public void onClose(InventoryCloseEvent event) {
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}

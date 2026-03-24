package ru.merkii.rduels.gui.internal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.HashMap;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class GUIHolder implements InventoryHolder {

    final Map<Integer, GUIItem> guiItems = new HashMap<>();
    final InventoryContext baseContext;
    @Setter
    Inventory inventory;

    public void setItem(int slot, GUIItem item) {
        guiItems.put(slot, item);
    }

    public GUIItem getGuiItem(int slot) {
        return guiItems.get(slot);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

}

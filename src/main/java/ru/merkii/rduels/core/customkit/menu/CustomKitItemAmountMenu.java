package ru.merkii.rduels.core.customkit.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomKitItemAmountMenu {

    private final InventoryGUIFactory factory;

    public CustomKitItemAmountMenu() {
        this.factory = RDuels.beanScope().get(InventoryGUIFactory.class);
    }

    public void open(Player player, String kitName, int slot, Material material, Collection<ItemBuilder> items) {
        Map<String, Object> raw = new HashMap<>();
        raw.put("kit_name", kitName);
        raw.put("slot", slot);
        raw.put("material", material);
        raw.put("items", items);
        InventoryContext context = InventoryContext.create(raw);
        factory.create("item-amount", player, context).ifPresent(InventoryGUI::open);
    }
}

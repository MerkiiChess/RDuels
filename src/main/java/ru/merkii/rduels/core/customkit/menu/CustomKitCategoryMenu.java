package ru.merkii.rduels.core.customkit.menu;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomKitCategoryMenu {

    private final InventoryGUIFactory factory;

    public CustomKitCategoryMenu() {
        this.factory = RDuels.beanScope().get(InventoryGUIFactory.class);
    }

    public void open(Player player, String kitName, List<CustomKitCategory> categories, int slot) {  // Для категорий
        Map<String, Object> raw = new HashMap<>();
        raw.put("kit_name", kitName);
        raw.put("slot", slot);
        raw.put("categories", categories);
        InventoryContext context = InventoryContext.create(raw);
        factory.create("category-menu", player, context).ifPresent(InventoryGUI::open);
    }

    public void open(Player player, String kitName, int slot, List<CustomKitEnchantCategory> enchantCategories) {  // Для enchant
        Map<String, Object> raw = new HashMap<>();
        raw.put("kit_name", kitName);
        raw.put("slot", slot);
        raw.put("enchant_categories", enchantCategories);
        InventoryContext context = InventoryContext.create(raw);
        factory.create("enchant-category", player, context).ifPresent(InventoryGUI::open);
    }
}

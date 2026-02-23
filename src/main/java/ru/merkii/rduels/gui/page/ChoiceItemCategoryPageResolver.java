package ru.merkii.rduels.gui.page;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;

import java.util.List;

public class ChoiceItemCategoryPageResolver implements PageResolver<InventoryItem> {

    @Override
    public List<InventoryItem> resolve(Player player, InventoryContext context) {
        CustomKitCategory category = context.require("category");
        return category.getItems();
    }
}

package ru.merkii.rduels.gui.extractor;

import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;

public class ChoiceItemCategoryValueExtractor implements ValueExtractor {
    @Override
    public String extract(InventoryContext context, String text, Object model) {
        if (!(model instanceof InventoryItem inventoryItem)) {
            return text;
        }
        return text.replace("%category_material%", inventoryItem.material());
    }
}

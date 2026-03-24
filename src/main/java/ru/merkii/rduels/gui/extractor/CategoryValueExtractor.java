package ru.merkii.rduels.gui.extractor;

import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;

public class CategoryValueExtractor implements ValueExtractor {

    @Override
    public String extract(InventoryContext context, String text, Object model) {
        if (!(model instanceof CustomKitCategory customKitCategory)) {
            return text;
        }
        return text.replace("%category_material%", customKitCategory.getDisplayMaterial().name()).replace("%category_name%", customKitCategory.getDisplayName());
    }
}

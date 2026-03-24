package ru.merkii.rduels.gui.extractor;

import org.bukkit.Material;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;

public class ChoiceItemCategoryValueExtractor implements ValueExtractor {
    @Override
    public String extract(InventoryContext context, String text, Object model) {
        if (!(model instanceof Material material)) {
            return text;
        }
        return text.replace("%category_material%", material.name());
    }
}

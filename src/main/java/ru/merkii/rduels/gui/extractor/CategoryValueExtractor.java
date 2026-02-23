package ru.merkii.rduels.gui.extractor;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;

public class CategoryValueExtractor implements ValueExtractor {

    @Override
    public String extract(InventoryContext context, String text, Object model) {
        if (!(model instanceof CustomKitCategory customKitCategory)) {
            return text;
        }
        return text.replace("%category_material%", customKitCategory.getItem().material()).replace("%category_name%", PlainTextComponentSerializer.plainText().serialize(customKitCategory.getItem().name().get()));
    }
}

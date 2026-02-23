package ru.merkii.rduels.gui.extractor;

import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;
import ru.merkii.rduels.model.DuelOptionModel;

public class DuelOptionValueExtractor implements ValueExtractor {
    @Override
    public String extract(InventoryContext context, String text, Object model) {
        if (!(model instanceof DuelOptionModel duelOptionModel)) {
            return text;
        }
        ItemBuilder item = duelOptionModel.itemBuilder();
        return text.replace("%option_material%", item.getMaterial().name()).replace("%option_name%", item.getDisplayName());
    }
}

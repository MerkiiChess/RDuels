package ru.merkii.rduels.gui.extractor;

import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;
import ru.merkii.rduels.model.SlotModel;

public class SlotValueExtractor implements ValueExtractor {

    @Override
    public String extract(InventoryContext context, String text, Object modelObj) {
        if (!(modelObj instanceof SlotModel model)) return text;
        return text.replace("%slot_material%", model.item().getType().name()).replace("%slot_name%", model.name());
    }
}
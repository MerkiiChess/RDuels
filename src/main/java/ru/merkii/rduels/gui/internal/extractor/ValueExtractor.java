package ru.merkii.rduels.gui.internal.extractor;

import ru.merkii.rduels.gui.internal.context.InventoryContext;

public interface ValueExtractor {

    String extract(InventoryContext context, String text, Object model);

}

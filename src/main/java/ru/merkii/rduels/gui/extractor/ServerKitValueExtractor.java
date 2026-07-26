package ru.merkii.rduels.gui.extractor;

import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;
import ru.merkii.rduels.model.KitModel;

/** Resolves %server_kit_*% placeholders for KitModel page items (queue menu). */
public class ServerKitValueExtractor implements ValueExtractor {

    @Override
    public String extract(InventoryContext context, String text, Object model) {
        if (!(model instanceof KitModel kitModel)) {
            return text;
        }
        return text
                .replace("%server_kit_name%", kitModel.getDisplayName())
                .replace("%server_kit_material%", kitModel.getDisplayMaterial().name());
    }
}

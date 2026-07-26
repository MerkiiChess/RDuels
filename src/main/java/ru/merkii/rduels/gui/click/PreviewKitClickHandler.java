package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.preview.KitPreviewMenu;
import ru.merkii.rduels.model.DuelOptionModel;
import ru.merkii.rduels.model.KitModel;

/**
 * Shift-click on a kit option opens a read-only preview of that kit's items.
 */
public class PreviewKitClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "PREVIEW_KIT";

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        Object model = context.get("model").orElse(null);
        if (model instanceof DuelOptionModel option && "kit".equals(option.type())
                && option.model() instanceof KitModel kit) {
            KitPreviewMenu.open(player, kit);
        }
    }
}

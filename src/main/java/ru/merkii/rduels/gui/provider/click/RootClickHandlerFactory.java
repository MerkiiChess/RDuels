package ru.merkii.rduels.gui.provider.click;

import io.avaje.inject.BeanScope;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.menu.settings.gui.ClickSettings;
import ru.merkii.rduels.config.menu.settings.gui.RootClickSettings;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.ClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerFactory;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.model.SlotModel;

import java.util.Optional;


public class RootClickHandlerFactory implements ClickHandlerFactory {

    @Override
    public ClickHandler create(InventoryContext context) {
        RootClickSettings settings = context.require("settings");
        ClickType type = context.require("click_type");

        Optional<ClickSettings> specific = switch (type) {
            case LEFT        -> settings.left();
            case RIGHT       -> settings.right();
            case SHIFT_LEFT  -> settings.shiftLeft();
            case SHIFT_RIGHT -> settings.shiftRight();
            default          -> Optional.empty();
        };

        ClickSettings toUse = specific.orElse(settings);
        return createHandler(context, toUse);
    }

    private ClickHandler extendHandler(ClickHandler clickHandler, InventoryContext context, ClickSettings settings) {
        if (settings == null)
            return clickHandler;
        return clickHandler.push(createHandler(context, settings));
    }

    private ClickHandler createHandler(InventoryContext context, ClickSettings settings) {
        BeanScope beanScope = RDuels.beanScope();
        ClickHandler clickHandler = ClickHandler.empty();
        Player clicker = context.require("player");

        context.get("model").ifPresent(model -> {
            if (model instanceof CustomKitModel kitModel) {
                context.extend("kit_name", kitModel.getDisplayName());
            }
            if (model instanceof SlotModel slotModel) {
                context.overrideOrCreate("slot-id", slotModel.slot());
            }
        });

        if (settings.callback() != null) {
            ClickHandler callbackClickHandler = beanScope.get(ClickHandlerRegistry.class)
                    .findAndCreate(settings.callback(), context)
                    .orElseThrow();

            clickHandler = clickHandler.push(callbackClickHandler);
        }

        if (settings.open() != null) {
            clickHandler = clickHandler.push(() -> {
                InventoryContext newContext = context.copy();

                InventoryGUI gui = beanScope.get(InventoryGUIFactory.class)
                        .create(settings.open(), clicker, newContext)
                        .orElseThrow(() -> new IllegalStateException("Cannot find inventory with name '" + settings.open() + "'"));
                gui.open();
            });
        }

        if (settings.executeCommands() != null) {
            clickHandler = clickHandler.push(() -> settings.executeCommands().forEach(executeCommand -> executeCommand.execute(clicker)));
        }
        return clickHandler;
    }

}
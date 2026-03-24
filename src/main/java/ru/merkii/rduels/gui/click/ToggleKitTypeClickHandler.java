package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public record ToggleKitTypeClickHandler(
        InventoryGUIFactory factory
) implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "TOGGLE_KIT_TYPE";

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        String currentType = context.get("kit_type")
                .map(Object::toString)
                .orElse("SERVER");
        String newType = currentType.equals("SERVER") ? "CUSTOM" : "SERVER";
        InventoryContext newContext = context.copy();
        newContext.overrideOrCreate("kit_type", newType);
        factory.create("duel-choice-kit", player, newContext).ifPresent(InventoryGUI::open);
    }
}
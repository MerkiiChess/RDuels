package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public class ChoiceItemClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "CHOICE_ITEM";
    private final InventoryGUIFactory factory;

    public ChoiceItemClickHandler(InventoryGUIFactory factory) {
        this.factory = factory;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        CustomKitCategory category = context.require("model");

        InventoryContext newContext = context.copy();
        newContext.extend("category", category);
        factory.create("choice-item-category-menu", player, newContext).ifPresent(InventoryGUI::open);
    }
}

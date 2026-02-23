package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public class DuelRequestArenaClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "DUEL_REQUEST_ARENA_OPTION";
    private final InventoryGUIFactory factory;

    public DuelRequestArenaClickHandler(InventoryGUIFactory factory) {
        this.factory = factory;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        InventoryContext newContext = context.copy();
        newContext.overrideOrCreate("option_type", "arena");
        factory.create("duel-request", player, newContext).ifPresent(InventoryGUI::open);
    }
}

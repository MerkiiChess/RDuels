package ru.merkii.rduels.core.party.menu;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public class PartyFightMenu {

    private final InventoryGUIFactory factory;

    public PartyFightMenu() {
        this.factory = RDuels.beanScope().get(InventoryGUIFactory.class);
    }

    public void open(Player player) {
        InventoryContext context = InventoryContext.empty();
        factory.create("party-fight", player, context).ifPresent(InventoryGUI::open);
    }
}

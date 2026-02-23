package ru.merkii.rduels.core.duel.menu;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.HashMap;
import java.util.Map;

public class DuelChoiceKitMenu {

    private final InventoryGUIFactory factory;

    public DuelChoiceKitMenu() {
        this.factory = RDuels.beanScope().get(InventoryGUIFactory.class);
    }

    public void open(Player player, DuelRequest duelRequest, boolean ffa) {
        Map<String, Object> raw = new HashMap<>();
        raw.put("duel_request", duelRequest);
        raw.put("ffa", ffa);
        InventoryContext context = InventoryContext.create(raw);
        factory.create("duel-choice-kit", player, context).ifPresent(InventoryGUI::open);
    }
}

package ru.merkii.rduels.core.queue.menu;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.HashMap;
import java.util.Map;

public class QueueMenu {

    private final InventoryGUIFactory factory;

    public QueueMenu() {
        this.factory = RDuels.beanScope().get(InventoryGUIFactory.class);
    }

    public void open(Player player) {
        Map<String, Object> raw = new HashMap<>();
        raw.put("player", player);
        InventoryContext context = InventoryContext.create(raw);
        factory.create("queue", player, context).ifPresent(InventoryGUI::open);
    }
}

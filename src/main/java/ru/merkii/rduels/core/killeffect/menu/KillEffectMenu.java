package ru.merkii.rduels.core.killeffect.menu;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.HashMap;
import java.util.Map;

public class KillEffectMenu {

    private final InventoryGUIFactory factory;

    public KillEffectMenu() {
        this.factory = RDuels.beanScope().get(InventoryGUIFactory.class);
    }

    public void open(Player player) {
        Map<String, Object> raw = new HashMap<>();
        raw.put("player", player);
        InventoryContext context = InventoryContext.create(raw);
        factory.create("kill-effects", player, context).ifPresent(InventoryGUI::open);
    }
}

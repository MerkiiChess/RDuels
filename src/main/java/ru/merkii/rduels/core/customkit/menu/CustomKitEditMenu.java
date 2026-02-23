package ru.merkii.rduels.core.customkit.menu;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import java.util.HashMap;
import java.util.Map;

public class CustomKitEditMenu {

    private final InventoryGUIFactory factory;

    public CustomKitEditMenu() {
        this.factory = RDuels.beanScope().get(InventoryGUIFactory.class);
    }

    public void open(Player player, String kitName) {
        Map<String, Object> raw = new HashMap<>();
        raw.put("kit_name", kitName);
        InventoryContext context = InventoryContext.create(raw);
        factory.create("edit-kit", player, context).ifPresent(InventoryGUI::open);
    }

}

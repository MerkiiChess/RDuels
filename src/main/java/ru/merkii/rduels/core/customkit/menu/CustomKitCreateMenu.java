package ru.merkii.rduels.core.customkit.menu;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class CustomKitCreateMenu {

    private final InventoryGUIFactory factory;

    public CustomKitCreateMenu() {
        this.factory = RDuels.beanScope().get(InventoryGUIFactory.class);
    }

    public void open(Player player) {
        Map<String, Object> raw = new HashMap<>();
        raw.put("player", player);
        InventoryContext context = InventoryContext.create(raw);
        Optional<InventoryGUI> inventoryGUI = factory.create("create-kit", player, context);
        if (inventoryGUI.isEmpty()) {
            player.sendMessage("no work");
        } else {
            inventoryGUI.get().open();
        }
    }
}

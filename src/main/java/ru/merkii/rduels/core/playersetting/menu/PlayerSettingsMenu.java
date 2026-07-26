package ru.merkii.rduels.core.playersetting.menu;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.HashMap;
import java.util.Map;

public class PlayerSettingsMenu {

    private final InventoryGUIFactory factory;

    public PlayerSettingsMenu() {
        this.factory = RDuels.beanScope().get(InventoryGUIFactory.class);
    }

    public void open(Player player) {
        Map<String, Object> raw = new HashMap<>();
        // The state extractor needs the player during the initial render.
        raw.put("player", player);
        InventoryContext context = InventoryContext.create(raw);
        factory.create("player-settings", player, context).ifPresent(InventoryGUI::open);
    }
}

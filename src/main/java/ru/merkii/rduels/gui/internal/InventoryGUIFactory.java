package ru.merkii.rduels.gui.internal;

import org.bukkit.entity.Player;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.Optional;

public interface InventoryGUIFactory {

    Optional<InventoryGUI> create(String guiName, Player player, InventoryContext context);

}

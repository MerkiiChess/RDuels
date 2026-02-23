package ru.merkii.rduels.gui.internal.paged;

import org.bukkit.entity.Player;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.List;

public interface PageResolver<T> {

    List<T> resolve(Player player, InventoryContext context);

}

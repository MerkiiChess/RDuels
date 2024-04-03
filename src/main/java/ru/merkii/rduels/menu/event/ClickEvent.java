package ru.merkii.rduels.menu.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.builder.ItemBuilder;

@Getter
@AllArgsConstructor
public class ClickEvent {

    private final Player player;
    private final int slot;
    private final boolean isPlayerInventoryClick;
    private final ItemStack clickedItem;
    private final ItemBuilder itemBuilder;
    private final boolean isShiftClick;
    private final boolean isRightClick;
    private final boolean isLeftClick;

}

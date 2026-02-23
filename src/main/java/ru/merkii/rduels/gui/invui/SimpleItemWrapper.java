package ru.merkii.rduels.gui.invui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Click;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.function.Consumer;

public class SimpleItemWrapper extends SimpleItem {

    private Consumer<Click> clickHandler;

    public SimpleItemWrapper(@NotNull ItemProvider itemProvider) {
        super(itemProvider);
    }

    public SimpleItemWrapper(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickHandler == null)
            return;
        clickHandler.accept(new Click(event));
    }

    public SimpleItemWrapper setClickHandler(Consumer<Click> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

}

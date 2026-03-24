package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.gui.internal.GUIItem;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public record SelectCategoryClickHandler(CustomKitStorage customKitStorage, InventoryGUIFactory factory,
                                         MenuConfiguration config) implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "SELECT_CATEGORY";

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        String kitName = context.require("kit_name");
        Integer clickedSlot = context.require("clicked_slot");

        GUIItem item = context.require("item");
        ItemStack clickedItem = new ItemStack(item.itemStack());

        if (clickedItem.getType().isAir()) {
            return;
        }
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        customKitStorage.setItemSlot(clickedItem, kitName, clickedSlot, duelPlayer);
        InventoryContext newContext = context.copy();
        factory.create("edit-kit", player, newContext)
                .ifPresent(InventoryGUI::open);

        config.settings().notification().playSound(player, "edit-item");
    }
}

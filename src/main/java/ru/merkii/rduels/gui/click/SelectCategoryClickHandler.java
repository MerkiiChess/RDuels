package ru.merkii.rduels.gui.click;

import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import xyz.xenondevs.invui.item.Item;

public class SelectCategoryClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "SELECT_CATEGORY";

    private final CustomKitStorage customKitStorage;
    private final InventoryGUIFactory factory;
    private final MenuConfiguration config;

    @Inject
    public SelectCategoryClickHandler(CustomKitStorage customKitStorage, InventoryGUIFactory factory, MenuConfiguration config) {
        this.customKitStorage = customKitStorage;
        this.factory = factory;
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        String kitName = context.require("kit_name");
        Integer clickedSlot = context.require("clicked_slot");

        Item item = context.require("item");
        ItemStack clickedItem = new ItemStack(item.getItemProvider().get().getType());

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

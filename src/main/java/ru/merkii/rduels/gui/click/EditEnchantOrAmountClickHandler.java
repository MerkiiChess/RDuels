package ru.merkii.rduels.gui.click;

import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.invui.SimpleItemWrapper;

public class EditEnchantOrAmountClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "EDIT_ENCHANT_OR_AMOUNT";
    private final MenuConfiguration config;
    private final InventoryGUIFactory factory;

    @Inject
    public EditEnchantOrAmountClickHandler(MenuConfiguration config, InventoryGUIFactory factory) {
        this.config = config;
        this.factory = factory;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        InventoryItem inventoryItem = context.require("inventory_item");
        ItemStack item = new ItemStack(inventoryItem.bukkitMaterial());
        String kitName = context.require("kit_name");

        InventoryContext newContext = context.copy();
        newContext.extend("item_stack", item);
        newContext.extend("kit_name", kitName);

        if (isEnchantable(item)) {
            factory.create("enchant-category", player, newContext).ifPresent(InventoryGUI::open);
        } else {
            factory.create("item-amount", player, newContext).ifPresent(InventoryGUI::open);
        }
        config.settings().notification().playSound(player, "edit-item");
    }

    private boolean isEnchantable(ItemStack item) {
        return item.getType().getMaxStackSize() == 1;
    }
}

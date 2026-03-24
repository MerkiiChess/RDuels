package ru.merkii.rduels.gui.click;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.provider.BaseInventoryGUI;

import java.util.Map;

public record RemoveItemClickHandler(MenuConfiguration config) implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "REMOVE_ITEM";

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        String kitName = context.require("kit_name");
        int slot = (int) context.get("slot").orElse(-1);
        CustomKitStorage customKitStorage = RDuels.beanScope().get(CustomKitStorage.class);
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        Map<Integer, ItemStack> items = customKitStorage.getAllItemsKit(duelPlayer, kitName);
        if (!items.containsKey(slot) || items.get(slot) == null) {
            return;
        }
        customKitStorage.setItemSlot(new ItemStack(Material.AIR), kitName, slot, duelPlayer);
        config.settings().notification().playSound(player, "remove-item");
        config.messages().sendTo(player, Placeholder.wrapped("%slot%", String.valueOf(slot)), "item-removed");

        BaseInventoryGUI gui = context.require("gui");
        InventoryGUIFactory factory = RDuels.beanScope().get(InventoryGUIFactory.class);
        gui.updateAll(factory);
    }
}

package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.HashMap;
import java.util.Map;

public record SetAmountClickHandler(MenuConfiguration config, InventoryGUIFactory factory) implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "SET_AMOUNT";

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        int amount = context.require("amount");
        String kitName = context.require("kit_name");
        int slot = (int) context.get("slot").orElse(-1);
        CustomKitStorage customKitStorage = RDuels.beanScope().get(CustomKitStorage.class);
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        Map<Integer, ItemStack> items = customKitStorage.getAllItemsKit(duelPlayer, kitName);
        if (!items.containsKey(slot) || items.get(slot) == null) {
            return;
        }
        ItemStack item = items.get(slot);
        item.setAmount(amount);
        customKitStorage.setItemSlot(item, kitName, slot, duelPlayer);
        config.settings().notification().playSound(player, "set-amount");
        config.messages().sendTo(player, Placeholder.wrapped("%amount%", String.valueOf(amount)), "amount-set");

        Map<String, Object> raw = new HashMap<>();
        raw.put("kit_name", kitName);
        InventoryContext newContext = InventoryContext.create(raw);
        factory.create("edit-kit", player, newContext).ifPresent(InventoryGUI::open);
    }
}

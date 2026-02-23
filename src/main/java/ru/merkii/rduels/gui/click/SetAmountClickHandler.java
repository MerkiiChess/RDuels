package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.core.customkit.menu.CustomKitEditMenu;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public class SetAmountClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "SET_AMOUNT";
    private final MenuConfiguration config;

    public SetAmountClickHandler(MenuConfiguration config) {
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        int amount = context.require("amount");
        String kitName = context.require("kit_name");
        int slot = (int) context.get("slot").orElse(-1);
        CustomKitStorage customKitStorage = RDuels.beanScope().get(CustomKitStorage.class);
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        ItemStack item = customKitStorage.getItemFromSlot(slot, kitName, duelPlayer);
        item.setAmount(amount);
        customKitStorage.setItemSlot(item, kitName, slot, duelPlayer);
        config.settings().notification().playSound(player, "set-amount");
        config.messages().sendTo(player, Placeholder.wrapped("%amount%", String.valueOf(amount)), "amount-set");

        new CustomKitEditMenu().open(player, kitName);
    }
}

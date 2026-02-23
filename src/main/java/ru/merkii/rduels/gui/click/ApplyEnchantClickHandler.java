package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;
import ru.merkii.rduels.core.customkit.menu.CustomKitEditMenu;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public class ApplyEnchantClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "APPLY_ENCHANT";
    private final MenuConfiguration config;

    public ApplyEnchantClickHandler(MenuConfiguration config) {
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        CustomKitEnchantCategory enchantModel = context.require("model");
        String kitName = context.require("kit_name");
        int slot = context.require("slot");
        CustomKitStorage customKitStorage = RDuels.beanScope().get(CustomKitStorage.class);
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        ItemStack item = customKitStorage.addEnchantItem(slot, kitName, duelPlayer, enchantModel);
        if (item == null) {
            config.messages().sendTo(player, "no-item");
            return;
        }
        customKitStorage.setItemSlot(item, kitName, slot, duelPlayer);
        config.settings().notification().playSound(player, "edit-item");
        config.messages().sendTo(player, Placeholder.wrapped("%enchant_name%", enchantModel.getNameEnchant()), "enchant-applied");
        new CustomKitEditMenu().open(player, kitName);
    }
}

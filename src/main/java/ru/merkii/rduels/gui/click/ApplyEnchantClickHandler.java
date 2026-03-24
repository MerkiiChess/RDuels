package ru.merkii.rduels.gui.click;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.HashMap;
import java.util.Map;

public record ApplyEnchantClickHandler(MenuConfiguration config, InventoryGUIFactory factory) implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "APPLY_ENCHANT";

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        CustomKitEnchantCategory enchantModel = context.require("model");
        String kitName = context.require("kit_name");
        int slot = context.require("slot");
        CustomKitStorage customKitStorage = RDuels.beanScope().get(CustomKitStorage.class);
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        Map<Integer, ItemStack> items = customKitStorage.getAllItemsKit(duelPlayer, kitName);
        if (!items.containsKey(slot) || items.get(slot) == null) {
            return;
        }
        ItemStack item = items.get(slot);

        if (item == null || item.getType() == Material.AIR) {
            config.messages().sendTo(player, "no-item");
            return;
        }

        ItemStack enchantedItem = item.clone();
        ItemMeta meta = enchantedItem.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.getByName(enchantModel.getNameEnchant()),
                    enchantModel.getLvl(), true);
            enchantedItem.setItemMeta(meta);
        }

        customKitStorage.setItemSlot(enchantedItem, kitName, slot, duelPlayer);
        config.settings().notification().playSound(player, "edit-item");
        config.messages().sendTo(player, Placeholder.wrapped("%enchant_name%", enchantModel.getNameEnchant()), "enchant-applied");
        Map<String, Object> raw = new HashMap<>();
        raw.put("kit_name", kitName);
        InventoryContext newContext = InventoryContext.create(raw);
        factory.create("edit-kit", player, newContext).ifPresent(InventoryGUI::open);
    }
}

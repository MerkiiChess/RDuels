package ru.merkii.rduels.gui.preview;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.model.KitModel;

import java.util.Map;

/**
 * Opens a read-only inventory previewing a kit, laid out like the player screen:
 * armour column top-left, offhand next to it, main inventory in the middle and the
 * hotbar along the bottom row. Interactions are blocked by {@link KitPreviewListener}.
 * <p>
 * Server kits store items by {@code PlayerInventory} index: 0–8 hotbar, 9–35 inventory,
 * 36–39 armour (boots→helmet), 40 offhand.
 */
public final class KitPreviewMenu {

    private static final int SIZE = 54;
    private static final Material FILLER = Material.GRAY_STAINED_GLASS_PANE;

    // Kit (player-inventory) index -> preview slot.
    private static final int KIT_HELMET = 39, KIT_CHESTPLATE = 38, KIT_LEGGINGS = 37, KIT_BOOTS = 36, KIT_OFFHAND = 40;
    private static final int PREVIEW_HELMET = 0, PREVIEW_CHESTPLATE = 1, PREVIEW_LEGGINGS = 2, PREVIEW_BOOTS = 3, PREVIEW_OFFHAND = 5;
    private static final int INVENTORY_START = 9;  // kit 9..35 -> preview 9..35 (direct)
    private static final int HOTBAR_PREVIEW_START = 45; // kit 0..8 -> preview 45..53

    private KitPreviewMenu() {
    }

    public static void open(Player player, KitModel kit) {
        KitPreviewHolder holder = new KitPreviewHolder();
        Component title = MiniMessage.miniMessage().deserialize("<gray>Просмотр: " + kit.getDisplayName());
        Inventory inventory = Bukkit.createInventory(holder, SIZE, title);
        holder.setInventory(inventory);

        fillBackground(inventory);

        Map<Integer, ItemBuilder> items = kit.getItems();
        // Armour + offhand.
        place(inventory, items, KIT_HELMET, PREVIEW_HELMET);
        place(inventory, items, KIT_CHESTPLATE, PREVIEW_CHESTPLATE);
        place(inventory, items, KIT_LEGGINGS, PREVIEW_LEGGINGS);
        place(inventory, items, KIT_BOOTS, PREVIEW_BOOTS);
        place(inventory, items, KIT_OFFHAND, PREVIEW_OFFHAND);
        // Main inventory (kit 9..35 -> same preview slots).
        for (int kitSlot = 9; kitSlot <= 35; kitSlot++) {
            place(inventory, items, kitSlot, kitSlot);
        }
        // Hotbar (kit 0..8 -> preview 45..53).
        for (int kitSlot = 0; kitSlot <= 8; kitSlot++) {
            place(inventory, items, kitSlot, HOTBAR_PREVIEW_START + kitSlot);
        }

        holder.setInventory(inventory);
        player.openInventory(inventory);
    }

    private static void place(Inventory inventory, Map<Integer, ItemBuilder> items, int kitSlot, int previewSlot) {
        ItemBuilder builder = items.get(kitSlot);
        if (builder != null) {
            inventory.setItem(previewSlot, builder.build());
        } else {
            inventory.setItem(previewSlot, null);
        }
    }

    private static void fillBackground(Inventory inventory) {
        ItemStack pane = new ItemStack(FILLER);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(" "));
            pane.setItemMeta(meta);
        }
        // Separator slots around the armour/offhand row and its bottom border.
        for (int slot : new int[]{4, 6, 7, 8, 36, 37, 38, 39, 40, 41, 42, 43, 44}) {
            inventory.setItem(slot, pane);
        }
    }
}

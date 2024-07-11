package ru.merkii.rduels.core.customkit.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.customkit.category.CustomKitCategoryEnchantItemType;
import ru.merkii.rduels.core.customkit.config.CustomKitConfig;
import ru.merkii.rduels.menu.VMenu;
import ru.merkii.rduels.menu.event.ClickEvent;
import ru.merkii.rduels.util.ColorUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomKitEditMenu extends VMenu {

    private final CustomKitConfig customKitConfig = CustomKitCore.INSTANCE.getCustomKitConfig();
    private final CustomKitConfig.CategoriesMenu categoriesMenu = this.customKitConfig.getCategoriesMenu();
    private final CustomKitConfig.EditMenu editMenu = this.customKitConfig.getEditMenu();
    private final Map<Integer, ItemStack> allItemsKit;
    private final List<Integer> itemsSlot = new ArrayList<Integer>();
    private final String kitName;

    public CustomKitEditMenu(String kitName, Player player) {
        super(CustomKitCore.INSTANCE.getCustomKitConfig().getEditMenu().getSize(), ColorUtil.color(CustomKitCore.INSTANCE.getCustomKitConfig().getTitleEdit().replace("(kit)", kitName)));
        this.allItemsKit = CustomKitCore.INSTANCE.getCustomKitStorage().getAllItemsKit(player, kitName);
        this.kitName = kitName;
        this.allItemsKit.forEach((key, value) -> {
            if (value.getType() != Material.AIR) {
                this.setItem((int)key, ItemBuilder.builder().fromItemStack((ItemStack)value));
                this.itemsSlot.add((Integer)key);
            }
        });
        for (int i = 0; i <= 40; ++i) {
            if (this.itemsSlot.contains(i)) continue;
            if (i <= 8) {
                this.setItem(i, this.editMenu.getNoSlotMainLine());
                continue;
            }
            if (i <= 35) {
                this.setItem(i, this.editMenu.getNoSlotOtherLines());
                continue;
            }
            if (i <= 39) {
                if (i == 39) {
                    this.setItem(i, this.editMenu.getNoSlotBoots());
                    continue;
                }
                if (i == 38) {
                    this.setItem(i, this.editMenu.getNoSlotLeggings());
                    continue;
                }
                if (i == 37) {
                    this.setItem(i, this.editMenu.getNoSlotChestplate());
                    continue;
                }
                this.setItem(i, this.editMenu.getNoSlotHelmet());
                continue;
            }
            this.setItem(i, this.editMenu.getNoSlotOffHand());
        }
        this.setItem(53, this.editMenu.getExit());
    }

    @Override
    public void onClick(ClickEvent event) {
        ItemStack clickedItem = event.getClickedItem();
        Player player = event.getPlayer();
        if (clickedItem.equals(this.editMenu.getExit().build())) {
            new CustomKitCreateMenu(player).open(player);
            return;
        }
        if (event.isShiftClick()) {
            CustomKitCore.INSTANCE.getCustomKitStorage().setItemSlot(new ItemStack(Material.AIR), this.kitName, event.getSlot(), player);
            new CustomKitEditMenu(this.kitName, player).open(player);
            return;
        }
        if (this.allItemsKit.get(event.getSlot()) == null || event.isLeftClick()) {
            if (event.isRightClick()) {
                return;
            }
            if (event.getItemBuilder().equals(this.editMenu.getNoSlotChestplate()) || event.getItemBuilder().equals(this.editMenu.getNoSlotBoots()) || event.getItemBuilder().equals(this.editMenu.getNoSlotHelmet()) || event.getItemBuilder().equals(this.editMenu.getNoSlotLeggings())) {
                new CustomKitCategoryMenu(this.categoriesMenu.getCategories().stream().filter(customKitCategory -> customKitCategory.getItem().getMaterial() == this.categoriesMenu.getMaterialArmorCategory()).findFirst().get(), this.kitName, event.getSlot()).open(player);
                return;
            }
            new CustomKitCategoryMenu(this.categoriesMenu.getCategories(), this.kitName, event.getSlot()).open(player);
            return;
        }
        if (event.getItemBuilder().equals(this.editMenu.getNoSlotMainLine()) || event.getItemBuilder().equals(this.editMenu.getNoSlotOtherLines()) || event.getItemBuilder().equals(this.editMenu.getNoSlotChestplate()) || event.getItemBuilder().equals(this.editMenu.getNoSlotBoots()) || event.getItemBuilder().equals(this.editMenu.getNoSlotHelmet()) || event.getItemBuilder().equals(this.editMenu.getNoSlotLeggings()) || event.getItemBuilder().equals(this.editMenu.getNoSlotOffHand())) {
            return;
        }
        if (event.isRightClick()) {
            if (this.isSwordItem(clickedItem)) {
                new CustomKitCategoryMenu(this.kitName, event.getSlot(), this.categoriesMenu.getEnchantCategories().stream().filter(enchantCategory -> enchantCategory.getEnchantItemType() == CustomKitCategoryEnchantItemType.SWORD).collect(Collectors.toList())).open(player);
            } else if (this.isArmorItem(clickedItem)) {
                new CustomKitCategoryMenu(this.kitName, event.getSlot(), this.categoriesMenu.getEnchantCategories().stream().filter(enchantCategory -> enchantCategory.getEnchantItemType() == CustomKitCategoryEnchantItemType.ARMOR).collect(Collectors.toList())).open(player);
            } else if (this.isAxeItem(clickedItem)) {
                new CustomKitCategoryMenu(this.kitName, event.getSlot(), this.categoriesMenu.getEnchantCategories().stream().filter(enchantCategory -> enchantCategory.getEnchantItemType() == CustomKitCategoryEnchantItemType.AXE).collect(Collectors.toList())).open(player);
            } else if (this.isPickaxeItem(clickedItem)) {
                new CustomKitCategoryMenu(this.kitName, event.getSlot(), this.categoriesMenu.getEnchantCategories().stream().filter(enchantCategory -> enchantCategory.getEnchantItemType() == CustomKitCategoryEnchantItemType.PICKAXE).collect(Collectors.toList())).open(player);
            } else {
                new CustomKitItemAmountMenu(this.kitName, event.getSlot(), clickedItem.getType(), this.categoriesMenu.getItemStacks().keySet()).open(player);
            }
        }
    }

    private boolean isAxeItem(ItemStack itemStack) {
        Material material = itemStack.getType();
        return material == Material.DIAMOND_AXE || material == Material.GOLDEN_AXE || material == Material.IRON_AXE || material == Material.NETHERITE_AXE || material == Material.STONE_AXE || material == Material.WOODEN_AXE;
    }

    private boolean isPickaxeItem(ItemStack itemStack) {
        Material material = itemStack.getType();
        return material.name().contains("PICKAXE");
    }

    private boolean isSwordItem(ItemStack itemStack) {
        return itemStack.getType().name().contains("SWORD");
    }

    private boolean isArmorItem(ItemStack itemStack) {
        String nameMaterial = itemStack.getType().name();
        return nameMaterial.contains("ELYTRA") || nameMaterial.contains("SHIELD") || nameMaterial.contains("BOOTS") || nameMaterial.contains("LEGGINGS") || nameMaterial.contains("CHESTPLATE") || nameMaterial.contains("HELMET");
    }

}

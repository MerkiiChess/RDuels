package ru.merkii.rduels.core.customkit.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    private final CustomKitConfig.CategoriesMenu categoriesMenu = customKitConfig.getCategoriesMenu();
    private final CustomKitConfig.EditMenu editMenu = customKitConfig.getEditMenu();
    private final Map<Integer, ItemStack> allItemsKit;
    private final List<Integer> itemsSlot = new ArrayList<>();
    private final String kitName;

    public CustomKitEditMenu(String kitName, Player player) {
        super(CustomKitCore.INSTANCE.getCustomKitConfig().getEditMenu().getSize(), ColorUtil.color(CustomKitCore.INSTANCE.getCustomKitConfig().getTitleEdit().replace("(kit)", kitName)));
        this.allItemsKit = CustomKitCore.INSTANCE.getCustomKitStorage().getAllItemsKit(player, kitName);
        this.kitName = kitName;
        this.allItemsKit.forEach((key, value) -> {
            if (value.getType() != Material.AIR) {
                this.inventory.setItem(key, value);
                itemsSlot.add(key);
            }
        });
        for (int i = 0; i <= 40; i++) {
            if (!itemsSlot.contains(i)) {
                if (i <= 8) {
                    setItem(i, this.editMenu.getNoSlotMainLine());
                } else if (i <= 35) {
                    setItem(i, this.editMenu.getNoSlotOtherLines());
                } else if (i <= 39) {
                    if (i == 39) setItem(i, this.editMenu.getNoSlotBoots());
                    else if (i == 38) setItem(i, this.editMenu.getNoSlotLeggings());
                    else if (i == 37) setItem(i, this.editMenu.getNoSlotChestplate());
                    else setItem(i, this.editMenu.getNoSlotHelmet());
                } else {
                    setItem(i, this.editMenu.getNoSlotOffHand());
                }
            }
        }
        setItem(53, this.editMenu.getExit());
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getClickedItem().equals(this.editMenu.getExit().build())) {
            new CustomKitCreateMenu(event.getPlayer()).open(event.getPlayer());
            return;
        }
        if (event.isShiftClick()) {
            CustomKitCore.INSTANCE.getCustomKitStorage().setItemSlot(new ItemStack(Material.AIR), this.kitName, event.getSlot(), event.getPlayer());
            new CustomKitEditMenu(this.kitName, event.getPlayer()).open(event.getPlayer());
            return;
        }
        if (this.allItemsKit.get(event.getSlot()) == null || event.isLeftClick()) {
            if (event.isRightClick()) {
                return;
            }
            new CustomKitCategoryMenu(this.categoriesMenu.getCategories(), this.kitName, event.getSlot()).open(event.getPlayer());
            return;
        }
        if (event.isRightClick()) {
            if (isSwordItem(event.getClickedItem())) {
                new CustomKitCategoryMenu(this.kitName, event.getSlot(), this.categoriesMenu.getEnchantCategories().stream().filter(enchantCategory -> enchantCategory.getEnchantItemType() == CustomKitCategoryEnchantItemType.SWORD).collect(Collectors.toList())).open(event.getPlayer());
            } else if (isArmorItem(event.getClickedItem())) {
                new CustomKitCategoryMenu(this.kitName, event.getSlot(), this.categoriesMenu.getEnchantCategories().stream().filter(enchantCategory -> enchantCategory.getEnchantItemType() == CustomKitCategoryEnchantItemType.ARMOR).collect(Collectors.toList())).open(event.getPlayer());
            } else {
                new CustomKitItemAmountMenu(this.kitName, event.getSlot(), event.getClickedItem().getType(), this.categoriesMenu.getItemStacks().keySet()).open(event.getPlayer());
            }
        }
    }

    public boolean isSwordItem(ItemStack itemStack) {
        return itemStack.getType().name().contains("SWORD");
    }

    public boolean isArmorItem(ItemStack itemStack) {
        String nameMaterial = itemStack.getType().name();
        return nameMaterial.contains("ELYTRA") || nameMaterial.contains("SHIELD") || nameMaterial.contains("BOOTS") || nameMaterial.contains("LEGGINGS") || nameMaterial.contains("CHESTPLATE") || nameMaterial.contains("HELMET");
    }

}

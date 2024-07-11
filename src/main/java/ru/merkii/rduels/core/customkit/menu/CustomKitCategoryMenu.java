package ru.merkii.rduels.core.customkit.menu;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;
import ru.merkii.rduels.core.customkit.config.CustomKitConfig;
import ru.merkii.rduels.menu.VMenu;
import ru.merkii.rduels.menu.event.ClickEvent;
import ru.merkii.rduels.util.ColorUtil;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CustomKitCategoryMenu extends VMenu {

    private final CustomKitCore core = CustomKitCore.INSTANCE;
    private final CustomKitConfig customKitConfig = this.core.getCustomKitConfig();
    private final CustomKitConfig.CategoriesMenu categoriesMenu = this.customKitConfig.getCategoriesMenu();
    private final int slot;
    private final String kitName;
    private List<CustomKitCategory> categories;
    private CustomKitCategory kitCategory;
    private List<CustomKitEnchantCategory> enchantCategories;

    public CustomKitCategoryMenu(List<CustomKitCategory> categories, String kitName, int slot) {
        super(CustomKitCore.INSTANCE.getCustomKitConfig().getCategoriesMenu().getSize(), ColorUtil.color(CustomKitCore.INSTANCE.getCustomKitConfig().getTitleEdit().replace("(kit)", kitName)));
        categories.forEach(category -> this.setItem(category.getItem()));
        this.categories = categories;
        this.kitName = kitName;
        this.slot = slot;
        this.inventory.setItem(53, this.categoriesMenu.getExit().build());
    }

    public CustomKitCategoryMenu(CustomKitCategory category, String kitName, int slot) {
        super(CustomKitCore.INSTANCE.getCustomKitConfig().getCategoriesMenu().getSize(), ColorUtil.color(CustomKitCore.INSTANCE.getCustomKitConfig().getTitleEdit().replace("(kit)", kitName)));
        category.getItems().forEach((key, value) -> this.inventory.setItem(key, new ItemStack(value.build())));
        this.kitCategory = category;
        this.kitName = kitName;
        this.slot = slot;
        this.inventory.setItem(53, this.categoriesMenu.getExit().build());
    }

    public CustomKitCategoryMenu(String kitName, int slot, List<CustomKitEnchantCategory> enchantCategories) {
        super(CustomKitCore.INSTANCE.getCustomKitConfig().getCategoriesMenu().getSize(), ColorUtil.color(CustomKitCore.INSTANCE.getCustomKitConfig().getTitleEdit().replace("(kit)", kitName)));
        enchantCategories.forEach(category -> {
            ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = itemStack.getItemMeta();
            meta.addEnchant(Objects.requireNonNull(Enchantment.getByName(category.getNameEnchant())), category.getLvl(), false);
            itemStack.setItemMeta(meta);
            this.inventory.setItem(category.getSlot(), itemStack);
        });
        this.kitName = kitName;
        this.slot = slot;
        this.enchantCategories = enchantCategories;
        this.inventory.setItem(53, this.categoriesMenu.getExit().build());
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getClickedItem().equals(this.categoriesMenu.getExit().build())) {
            new CustomKitEditMenu(this.kitName, event.getPlayer()).open(event.getPlayer());
            return;
        }
        if (this.categories != null) {
            this.getCategoryFromSlot(event.getSlot()).ifPresent(category -> new CustomKitCategoryMenu((CustomKitCategory)category, this.kitName, this.slot).open(event.getPlayer()));
            return;
        }
        if (this.enchantCategories != null) {
            ItemStack itemStack = this.core.getCustomKitStorage().addEnchantItem(this.slot, this.kitName, event.getPlayer(), this.enchantCategories.get(event.getSlot()));
            if (itemStack == null) {
                RDuels.getInstance().getLogger().info("ItemStack is null");
                return;
            }
            this.core.getCustomKitStorage().setItemSlot(itemStack, this.kitName, this.slot, event.getPlayer());
            new CustomKitEditMenu(this.kitName, event.getPlayer()).open(event.getPlayer());
            return;
        }
        this.core.getCustomKitStorage().setItemSlot(this.kitCategory.getItems().get(event.getSlot()).build(), this.kitName, this.slot, event.getPlayer());
        new CustomKitEditMenu(this.kitName, event.getPlayer()).open(event.getPlayer());
    }

    private Optional<CustomKitCategory> getCategoryFromSlot(int slot) {
        return this.categories.stream().filter(category -> category.getItem().getSlot() == slot).findFirst();
    }

}

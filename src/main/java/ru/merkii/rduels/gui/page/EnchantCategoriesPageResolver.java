package ru.merkii.rduels.gui.page;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.config.enchant.EnchantItemConfiguration;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;

import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnchantCategoriesPageResolver implements PageResolver<CustomKitEnchantCategory> {

    List<CustomKitEnchantCategory> enchantCategories;

    public EnchantCategoriesPageResolver(EnchantItemConfiguration enchantItemConfiguration) {
        this.enchantCategories = enchantItemConfiguration.enchants();
    }

    @Override
    public List<CustomKitEnchantCategory> resolve(Player player, InventoryContext context) {
        ItemStack item = context.require("item_stack");
        String itemTypeName = item.getType().name();
        return enchantCategories.stream()
                .filter(category ->
                        category.getMaterialsEnchanted().stream()
                            .anyMatch(material -> itemTypeName.contains(material) || itemTypeName.endsWith(material)))
                .collect(Collectors.toList());
    }
}

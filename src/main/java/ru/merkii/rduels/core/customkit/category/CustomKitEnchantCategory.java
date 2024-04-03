package ru.merkii.rduels.core.customkit.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomKitEnchantCategory {

    private String nameEnchant;
    private int lvl;
    private int slot;
    private CustomKitCategoryEnchantItemType enchantItemType;

    public static CustomKitEnchantCategory create(String nameEnchant, int lvl, int slot, CustomKitCategoryEnchantItemType enchantItemType) {
        return new CustomKitEnchantCategory(nameEnchant, lvl, slot, enchantItemType);
    }

}

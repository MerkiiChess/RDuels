package ru.merkii.rduels.model;

import org.bukkit.inventory.ItemStack;

public record SlotModel(int slot, ItemStack item, String name, String categoryId) {

}

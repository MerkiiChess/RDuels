package ru.merkii.rduels.gui.internal;

import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;

public record GUIItem(ItemStack itemStack, InventoryItem config, Object model) {}
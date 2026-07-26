package ru.merkii.rduels.core.bedwars.resource;

import jakarta.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Treats specific items in a player's inventory as spendable Bedwars currency. */
@Singleton
public class ResourceService {

    public int count(Player player, Material material) {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                total += item.getAmount();
            }
        }
        return total;
    }

    public boolean has(Player player, Material material, int amount) {
        return count(player, material) >= amount;
    }

    /** Removes {@code amount} of the material; returns false (removing nothing) if short. */
    public boolean take(Player player, Material material, int amount) {
        if (!has(player, material, amount)) {
            return false;
        }
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() != material) {
                continue;
            }
            int take = Math.min(remaining, item.getAmount());
            remaining -= take;
            if (take >= item.getAmount()) {
                player.getInventory().setItem(i, null);
            } else {
                item.setAmount(item.getAmount() - take);
            }
        }
        player.updateInventory();
        return true;
    }
}

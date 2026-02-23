package ru.merkii.rduels.core.customkit.storage;

import jakarta.inject.Singleton;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
public class CustomKitStorage {

    public FileConfiguration getConfig(DuelPlayer player) {
        File file = new File(RDuels.getInstance().getDataFolder(), "/players/" + player.getUUID() + ".yml");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    RDuels.getInstance().getLogger().info("Error creating file: " + player.getUUID() + ".yml");
                }
            } catch (IOException e) {
                RDuels.getInstance().getLogger().warning("Error creating file: " + player.getUUID() + ".yml");
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void setItemSlot(ItemStack itemStack, String kitName, int slot, DuelPlayer player) {
        FileConfiguration config = getConfig(player);
        ConfigurationSection section = config.getConfigurationSection(kitName);
        if (section == null) {
            section = config.createSection(kitName);
        }
        section.set(String.valueOf(slot), itemStack);
        save(config, player);
    }

    public void save(FileConfiguration config, DuelPlayer player) {
        try {
            config.save(new File(RDuels.getInstance().getDataFolder(), "/players/" + player.getUUID() + ".yml"));
        } catch (IOException ignored) {

        }
    }

    public Map<Integer, ItemStack> getAllItemsKit(DuelPlayer player, String kitName) {
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        FileConfiguration config = this.getConfig(player);
        if (config.getConfigurationSection(kitName) == null) {
            config.createSection(kitName);
            save(config, player);
        }
        config = this.getConfig(player);
        if (!config.getConfigurationSection(kitName).getKeys(false).isEmpty()) {
            for (String key : config.getConfigurationSection(kitName).getKeys(false)) {
                try {
                    itemStackMap.put(Integer.parseInt(key), config.getConfigurationSection(kitName).getItemStack(key));
                } catch (NumberFormatException ignored) {}
            }
        }
        return itemStackMap;
    }

    public ItemStack getItemFromSlot(int slot, String kitName, DuelPlayer player) {
       return this.getConfig(player).getConfigurationSection(kitName).getItemStack(String.valueOf(slot));
    }

    public ItemStack addEnchantItem(int slot, String kitName, DuelPlayer player, CustomKitEnchantCategory enchantCategory) {
        ItemStack itemStack = getItemFromSlot(slot, kitName, player);
        if (itemStack == null) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Objects.requireNonNull(Enchantment.getByName(enchantCategory.getNameEnchant())), enchantCategory.getLvl(), false);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}

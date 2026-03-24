package ru.merkii.rduels.core.customkit.storage;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomKitStorage {

    Map<UUID, Map<String, Map<Integer, ItemStack>>> kitCache = new ConcurrentHashMap<>();
    Map<UUID, String> selectedKitCache = new ConcurrentHashMap<>();

    public void loadKits(DuelPlayer player) {
        File file = getFile(player);
        if (!file.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String selected = config.getString("selectedKit", "NULL");
        selectedKitCache.put(player.getUUID(), selected);

        Map<String, Map<Integer, ItemStack>> playerKits = new HashMap<>();

        for (String kitName : config.getKeys(false)) {
            Map<Integer, ItemStack> items = new HashMap<>();
            var section = config.getConfigurationSection(kitName);
            if (section != null) {
                for (String slotKey : section.getKeys(false)) {
                    items.put(Integer.parseInt(slotKey), section.getItemStack(slotKey));
                }
            }
            playerKits.put(kitName, items);
        }
        kitCache.put(player.getUUID(), playerKits);
    }

    public Map<Integer, ItemStack> getAllItemsKit(DuelPlayer player, String kitName) {
        return kitCache.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                .getOrDefault(kitName, new HashMap<>());
    }

    public void setItemSlot(ItemStack item, String kitName, int slot, DuelPlayer player) {
        kitCache.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                .computeIfAbsent(kitName, k -> new HashMap<>())
                .put(slot, item);
        RDuels.getInstance().getServer().getScheduler().runTaskAsynchronously(RDuels.getInstance(), () -> {
            FileConfiguration config = YamlConfiguration.loadConfiguration(getFile(player));
            config.set(kitName + "." + slot, item);
            try {
                config.save(getFile(player));
            } catch (IOException ignored) {}
        });
    }

    private File getFile(DuelPlayer player) {
        return new File(RDuels.getInstance().getDataFolder(), "players/" + player.getUUID() + ".yml");
    }

    public void unload(UUID uuid) {
        kitCache.remove(uuid);
    }

    public void setSelectedKit(DuelPlayer player, String kit) {
        selectedKitCache.put(player.getUUID(), kit);

        RDuels.getInstance().getServer().getScheduler().runTaskAsynchronously(RDuels.getInstance(), () -> {
            File file = getFile(player);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("selectedKit", kit);
            try {
                config.save(file);
            } catch (IOException ignored) {}
        });
    }

    public String getSelectedKit(UUID uuid) {
        return selectedKitCache.getOrDefault(uuid, "NULL");
    }

}
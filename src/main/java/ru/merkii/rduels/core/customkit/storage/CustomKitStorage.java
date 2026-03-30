package ru.merkii.rduels.core.customkit.storage;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.util.PluginConsole;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomKitStorage {

    Map<UUID, Map<String, Map<Integer, ItemStack>>> kitCache = new ConcurrentHashMap<>();
    Map<UUID, Optional<String>> selectedKitCache = new ConcurrentHashMap<>();

    public void loadKits(DuelPlayer player) {
        File file = getFile(player);
        if (!file.exists()) {
            selectedKitCache.put(player.getUUID(), Optional.empty());
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        selectedKitCache.put(player.getUUID(), normalizeKitName(config.getString("selectedKit")));

        Map<String, Map<Integer, ItemStack>> playerKits = new HashMap<>();

        for (String kitName : config.getKeys(false)) {
            if ("selectedKit".equalsIgnoreCase(kitName)) {
                continue;
            }
            Map<Integer, ItemStack> items = new HashMap<>();
            var section = config.getConfigurationSection(kitName);
            if (section != null) {
                for (String slotKey : section.getKeys(false)) {
                    ItemStack itemStack = section.getItemStack(slotKey);
                    if (itemStack != null && !itemStack.getType().isAir()) {
                        items.put(Integer.parseInt(slotKey), itemStack);
                    }
                }
            }
            playerKits.put(kitName, items);
        }
        kitCache.put(player.getUUID(), playerKits);
    }

    public Map<Integer, ItemStack> getAllItemsKit(DuelPlayer player, String kitName) {
        return kitCache.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                .getOrDefault(kitName, Collections.emptyMap());
    }

    public void setItemSlot(ItemStack item, String kitName, int slot, DuelPlayer player) {
        Map<Integer, ItemStack> kitItems = kitCache.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                .computeIfAbsent(kitName, k -> new HashMap<>());

        if (item == null || item.getType().isAir()) {
            kitItems.remove(slot);
        } else {
            kitItems.put(slot, item);
        }

        RDuels.getInstance().getServer().getScheduler().runTaskAsynchronously(RDuels.getInstance(), () -> {
            File file = getFile(player);
            createParentDirectories(file);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (item == null || item.getType().isAir()) {
                config.set(kitName + "." + slot, null);
            } else {
                config.set(kitName + "." + slot, item);
            }
            try {
                config.save(file);
            } catch (IOException exception) {
                PluginConsole.warn(RDuels.getInstance(), "Не удалось сохранить кастомный кит игрока " + player.getName() + ".");
            }
        });
    }

    private File getFile(DuelPlayer player) {
        return new File(RDuels.getInstance().getDataFolder(), "players/" + player.getUUID() + ".yml");
    }

    public void unload(UUID uuid) {
        kitCache.remove(uuid);
        selectedKitCache.remove(uuid);
    }

    public void setSelectedKit(DuelPlayer player, String kit) {
        Optional<String> normalizedKit = normalizeKitName(kit);
        selectedKitCache.put(player.getUUID(), normalizedKit);

        RDuels.getInstance().getServer().getScheduler().runTaskAsynchronously(RDuels.getInstance(), () -> {
            File file = getFile(player);
            createParentDirectories(file);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("selectedKit", normalizedKit.orElse(null));
            try {
                config.save(file);
            } catch (IOException exception) {
                PluginConsole.warn(RDuels.getInstance(), "Не удалось сохранить выбранный кастомный кит игрока " + player.getName() + ".");
            }
        });
    }

    public Optional<String> getSelectedKit(UUID uuid) {
        return selectedKitCache.getOrDefault(uuid, Optional.empty());
    }

    private Optional<String> normalizeKitName(String rawKitName) {
        return Optional.ofNullable(rawKitName)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .filter(value -> !"NULL".equalsIgnoreCase(value));
    }

    private void createParentDirectories(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

}

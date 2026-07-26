package ru.merkii.rduels.core.bedwars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;
import java.util.Map;

/** One purchasable entry in the Bedwars item shop. */
@ConfigInterface
public interface BedwarsShopItem {

    int slot();

    /** Icon / given material (Bukkit name). */
    String material();

    /** Display name (MiniMessage); empty = vanilla item name. */
    default String name() {
        return "";
    }

    default List<String> lore() {
        return List.of();
    }

    /** Currency: IRON, GOLD, DIAMOND or EMERALD. */
    String costResource();

    int costAmount();

    default int amount() {
        return 1;
    }

    /** Enchantments applied to the given item, keyed by enchant name. */
    default Map<String, Integer> enchants() {
        return Map.of();
    }

    /**
     * Persistence category: "armor", "pickaxe", "axe", "shears" persist across respawns;
     * empty = a normal item that is lost on death.
     */
    default String category() {
        return "";
    }

    /** Tier index within its category (higher replaces lower), used for persistence. */
    default int tier() {
        return 0;
    }

}

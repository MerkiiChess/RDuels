package ru.merkii.rduels.core.skywars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

/** One entry of the Skywars loot table. */
@ConfigInterface
public interface SkywarsLootEntry {

    /** Bukkit material name. */
    String material();

    default int minAmount() {
        return 1;
    }

    default int maxAmount() {
        return 1;
    }

    /** Relative pick weight (higher = more common). */
    default int weight() {
        return 1;
    }

}

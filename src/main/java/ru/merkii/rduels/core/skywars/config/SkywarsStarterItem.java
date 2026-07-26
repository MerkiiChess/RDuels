package ru.merkii.rduels.core.skywars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.Map;

/** One item handed to players at Skywars spawn (overrides the kit when any are set). */
@ConfigInterface
public interface SkywarsStarterItem {

    String material();

    default int amount() {
        return 1;
    }

    /** Enchantments keyed by enchant name (e.g. SHARPNESS). */
    default Map<String, Integer> enchants() {
        return Map.of();
    }

}

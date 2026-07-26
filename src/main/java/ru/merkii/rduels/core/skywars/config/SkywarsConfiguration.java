package ru.merkii.rduels.core.skywars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface SkywarsConfiguration {

    boolean enabled();

    /** Half-size of the cuboid around the arena centre scanned for chests. Keep modest. */
    int searchRadius();

    /** Re-fill chests every N seconds while the fight lasts; 0 = fill once at start only. */
    int refillSeconds();

    /** Minimum / maximum number of item stacks placed into each chest. */
    int minItemsPerChest();

    int maxItemsPerChest();

    /** Chests within this distance of the arena centre use the better mid loot; 0 disables tiers. */
    default int midRadius() {
        return 0;
    }

    /** Announce and message when chests are refilled. */
    default boolean refillAnnounce() {
        return true;
    }

    default String refillMessage() {
        return "<yellow>Сундуки пополнены!";
    }

    /** Items given to each player on spawn; when non-empty they replace the chosen kit. */
    default List<SkywarsStarterItem> starterItems() {
        return List.of();
    }

    /** Weighted loot table for island (outer) chests. */
    List<SkywarsLootEntry> loot();

    /** Weighted loot table for central (mid) chests; falls back to {@link #loot()} when empty. */
    default List<SkywarsLootEntry> midLoot() {
        return List.of();
    }

}

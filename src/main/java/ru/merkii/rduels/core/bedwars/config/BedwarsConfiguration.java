package ru.merkii.rduels.core.bedwars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.Map;

@ConfigInterface
public interface BedwarsConfiguration {

    boolean enabled();

    /** Delay before a player respawns after death (while their bed stands), in seconds. */
    default int respawnSeconds() {
        return 4;
    }

    /** Half-size of the cuboid scanned around the arena centre for generator markers. */
    default int scanRadius() {
        return 40;
    }

    /**
     * Generator markers keyed by the marker block's material name (e.g. IRON_BLOCK).
     * The generator spawns its resource one block above the marker.
     */
    Map<String, GeneratorMarker> generators();

}

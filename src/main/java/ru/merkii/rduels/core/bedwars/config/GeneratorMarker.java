package ru.merkii.rduels.core.bedwars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

/** Maps a marker block (its material name is the config key) to a generator definition. */
@ConfigInterface
public interface GeneratorMarker {

    /** Resource produced: IRON, GOLD, DIAMOND or EMERALD. */
    String resource();

    /** Base spawn interval in ticks. */
    int interval();

    /** True = team base generator (assigned to the nearest team, sped up by Forge). */
    boolean team();

    /** Do not spawn more when this many of the resource item already lie nearby. */
    default int maxNearby() {
        return 48;
    }

}

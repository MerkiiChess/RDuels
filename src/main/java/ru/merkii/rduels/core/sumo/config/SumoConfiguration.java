package ru.merkii.rduels.core.sumo.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

@ConfigInterface
public interface SumoConfiguration {

    boolean enabled();

    /** Touching water counts as a loss (classic sumo). */
    boolean detectWater();

    /** Falling this many blocks below the arena spawn Y counts as a loss. */
    int fallDistance();

}

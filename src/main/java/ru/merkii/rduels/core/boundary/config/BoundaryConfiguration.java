package ru.merkii.rduels.core.boundary.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

@ConfigInterface
public interface BoundaryConfiguration {

    boolean enabled();

    /** Absolute Y below which a fighter is considered "in the void". */
    int voidMinY();

    /** What happens when a fighter reaches the void: TELEPORT_BACK or KILL (loses the round). */
    String voidAction();

    /** Horizontal distance from the arena centre beyond which a fighter is "out of bounds"; 0 disables. */
    int maxRadius();

    /** Distance (blocks) from the boundary at which to start warning the player; 0 disables warnings. */
    int warnDistance();

    /** Action-bar warning text (MiniMessage), placeholder (blocks). */
    String warnMessage();

}

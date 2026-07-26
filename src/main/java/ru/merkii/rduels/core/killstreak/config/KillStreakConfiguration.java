package ru.merkii.rduels.core.killstreak.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface KillStreakConfiguration {

    boolean enabled();

    /** true — announce to the whole server; false — only to the fight participants. */
    boolean broadcastGlobal();

    /** Milestones, ordered by {@code count} ascending. */
    List<KillStreakTier> tiers();

}

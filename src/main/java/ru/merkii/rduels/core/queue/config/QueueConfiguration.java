package ru.merkii.rduels.core.queue.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

@ConfigInterface
public interface QueueConfiguration {

    boolean enabled();

    /** Matchmaker tick period, in server ticks. */
    int matchIntervalTicks();

    /** Rounds per queue match. */
    int numGames();

    /** Ranked queue: initial allowed Elo difference between opponents. */
    int rankedEloRange();

    /** Ranked queue: the allowed difference grows by this many points per second of waiting. */
    int rankedEloRangePerSecond();

}

package ru.merkii.rduels.core.killstreak.bucket;

import jakarta.inject.Singleton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory current kill streak per player (reset on death, fight end and quit). */
@Singleton
public class KillStreakBucket {

    private final Map<UUID, Integer> streaks = new ConcurrentHashMap<>();

    public int increment(UUID uuid) {
        return streaks.merge(uuid, 1, Integer::sum);
    }

    public int get(UUID uuid) {
        return streaks.getOrDefault(uuid, 0);
    }

    public void reset(UUID uuid) {
        streaks.remove(uuid);
    }

}

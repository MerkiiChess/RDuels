package ru.merkii.rduels.core.rematch.bucket;

import jakarta.inject.Singleton;
import ru.merkii.rduels.model.KitModel;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Remembers each player's last 1v1 opponent and kit, for /rematch. */
@Singleton
public class RematchBucket {

    public record Rematch(UUID opponent, KitModel kitModel) {
    }

    private final Map<UUID, Rematch> lastMatches = new ConcurrentHashMap<>();

    public void record(UUID player, UUID opponent, KitModel kitModel) {
        lastMatches.put(player, new Rematch(opponent, kitModel));
    }

    public Optional<Rematch> get(UUID player) {
        return Optional.ofNullable(lastMatches.get(player));
    }

    public void clear(UUID player) {
        lastMatches.remove(player);
    }

}

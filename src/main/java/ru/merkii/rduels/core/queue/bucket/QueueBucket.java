package ru.merkii.rduels.core.queue.bucket;

import jakarta.inject.Singleton;
import lombok.Getter;
import ru.merkii.rduels.core.queue.model.QueueEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Runtime registry of players waiting in kit queues. */
@Getter
@Singleton
public class QueueBucket {

    private final List<QueueEntry> entries = new ArrayList<>();

    public void add(QueueEntry entry) {
        entries.add(entry);
    }

    public Optional<QueueEntry> findByPlayer(UUID uuid) {
        return entries.stream().filter(entry -> entry.getPlayer().equals(uuid)).findFirst();
    }

    public boolean remove(UUID uuid) {
        return entries.removeIf(entry -> entry.getPlayer().equals(uuid));
    }

    public void clear() {
        entries.clear();
    }

}

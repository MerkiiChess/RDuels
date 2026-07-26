package ru.merkii.rduels.core.queue.model;

import lombok.Getter;
import ru.merkii.rduels.model.KitModel;

import java.util.UUID;

@Getter
public class QueueEntry {

    private final UUID player;
    private final KitModel kitModel;
    private final boolean ranked;
    private final int eloAtJoin;
    private final long joinedAt = System.currentTimeMillis();

    public QueueEntry(UUID player, KitModel kitModel, boolean ranked, int eloAtJoin) {
        this.player = player;
        this.kitModel = kitModel;
        this.ranked = ranked;
        this.eloAtJoin = eloAtJoin;
    }

    public long waitSeconds() {
        return (System.currentTimeMillis() - joinedAt) / 1000L;
    }

}

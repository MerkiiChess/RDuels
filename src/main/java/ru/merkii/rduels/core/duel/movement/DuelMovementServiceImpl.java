package ru.merkii.rduels.core.duel.movement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.core.duel.bucket.DuelMoveBucket;

@Singleton
public class DuelMovementServiceImpl implements DuelMovementService {
    private final DuelMoveBucket duelMoveBucket;

    @Inject
    public DuelMovementServiceImpl(DuelMoveBucket duelMoveBucket) {
        this.duelMoveBucket = duelMoveBucket;
    }

    @Override
    public void addNoMove(DuelPlayer p) {
        if (!isNoMovePlayer(p))
            duelMoveBucket.add(p);
    }
    @Override
    public void removeNoMove(DuelPlayer p) {
        if (p != null && isNoMovePlayer(p))
            duelMoveBucket.remove(p);
    }
    @Override
    public boolean isNoMovePlayer(DuelPlayer p) {
        return duelMoveBucket.contains(p);
    }
}
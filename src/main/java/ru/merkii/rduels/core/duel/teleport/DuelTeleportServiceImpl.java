package ru.merkii.rduels.core.duel.teleport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.merkii.rduels.core.duel.bucket.DuelTeleportBucket;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.schedualer.DuelTeleportScheduler;

import java.util.Optional;

@Singleton
public class DuelTeleportServiceImpl implements DuelTeleportService {

    private final DuelTeleportBucket duelTeleportBucket;

    @Inject
    public DuelTeleportServiceImpl(DuelTeleportBucket duelTeleportBucket) {
        this.duelTeleportBucket = duelTeleportBucket;
    }

    @Override
    public Optional<DuelTeleportScheduler> getTeleportSchedulerFromFight(DuelFightModel duelFightModel) {
        return this.duelTeleportBucket.getTeleportSchedulerFromFight(duelFightModel);
    }
}
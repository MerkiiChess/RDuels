package ru.merkii.rduels.core.duel.teleport;

import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.schedualer.DuelTeleportScheduler;

import java.util.Optional;

public interface DuelTeleportService {

    Optional<DuelTeleportScheduler> getTeleportSchedulerFromFight(DuelFightModel duelFightModel);

}

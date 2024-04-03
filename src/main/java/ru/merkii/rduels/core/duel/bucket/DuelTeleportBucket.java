package ru.merkii.rduels.core.duel.bucket;

import lombok.Getter;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.schedualer.DuelTeleportScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class DuelTeleportBucket {

    private final List<DuelTeleportScheduler> duelTeleportSchedulerList = new ArrayList<>();

    public void add(DuelTeleportScheduler duelTeleportScheduler) {
        this.duelTeleportSchedulerList.add(duelTeleportScheduler);
    }

    public void remove(DuelTeleportScheduler duelTeleportScheduler) {
        this.duelTeleportSchedulerList.remove(duelTeleportScheduler);
    }

    public Optional<DuelTeleportScheduler> getTeleportSchedulerFromFight(DuelFightModel duelFightModel) {
        return this.duelTeleportSchedulerList.stream().filter(duelTeleportScheduler -> duelTeleportScheduler.getDuelFightModel().equals(duelFightModel)).findFirst();
    }

}

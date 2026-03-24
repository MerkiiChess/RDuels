package ru.merkii.rduels.core.duel.bucket;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.schedualer.DuelTeleportScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DuelTeleportBucket {

    List<DuelTeleportScheduler> duelTeleportSchedulerList = new ArrayList<>();

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

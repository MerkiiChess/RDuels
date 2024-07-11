package ru.merkii.rduels.core.duel.schedualer;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.util.TimeUtil;

import java.util.concurrent.TimeUnit;

public class DuelScheduler extends BukkitRunnable {

    private DuelFightModel duelFightModel;
    @Getter
    private long time;

    public DuelScheduler(long time, DuelFightModel duelFightModel) {
        this.time = time / 1000L;
        this.duelFightModel = duelFightModel;
        runTaskTimer(RDuels.getInstance(), 20L, 20L);
    }

    public static DuelScheduler create(long time, DuelFightModel duelFightModel) {
        return new DuelScheduler(time / 1000L, duelFightModel);
    }

    @Override
    public void run() {
        --time;
        if (time <= 1) {
            DuelCore.INSTANCE.getDuelAPI().stopFight(this.duelFightModel, null, null);
            this.cancel();
        }
    }

    public void updateTime(DuelFightModel duelFightModel) {
        this.time = TimeUtil.parseTime(RDuels.getInstance().getSettings().getDurationFight(), TimeUnit.MINUTES) / 1000L;
        this.duelFightModel = duelFightModel;
    }
}

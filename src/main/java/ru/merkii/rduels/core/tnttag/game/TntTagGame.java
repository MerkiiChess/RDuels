package ru.merkii.rduels.core.tnttag.game;

import ru.merkii.rduels.core.duel.model.DuelFightModel;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/** Live state of one TNT Tag round: who is alive, who holds the bomb and the fuse. */
public class TntTagGame {

    private final DuelFightModel fightModel;
    private final Set<UUID> alive = new LinkedHashSet<>();
    private UUID holder;
    private int fuseLeft;
    private long passLockUntil;
    private int taskId = -1;

    public TntTagGame(DuelFightModel fightModel) {
        this.fightModel = fightModel;
    }

    public DuelFightModel getFightModel() {
        return fightModel;
    }

    public Set<UUID> getAlive() {
        return alive;
    }

    public boolean isAlive(UUID uuid) {
        return alive.contains(uuid);
    }

    public UUID getHolder() {
        return holder;
    }

    public void setHolder(UUID holder) {
        this.holder = holder;
    }

    public boolean isHolder(UUID uuid) {
        return uuid.equals(holder);
    }

    public int getFuseLeft() {
        return fuseLeft;
    }

    public void setFuseLeft(int fuseLeft) {
        this.fuseLeft = fuseLeft;
    }

    public int decrementFuse() {
        return --fuseLeft;
    }

    public long getPassLockUntil() {
        return passLockUntil;
    }

    public void setPassLockUntil(long passLockUntil) {
        this.passLockUntil = passLockUntil;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}

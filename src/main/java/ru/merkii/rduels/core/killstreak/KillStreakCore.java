package ru.merkii.rduels.core.killstreak;

import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;

/**
 * Kill-streak module: tracks consecutive kills, announces milestones and runs their
 * reward commands. All behaviour lives in the auto-registered listener.
 */
@Singleton
public class KillStreakCore implements Core {

    @Override
    public void enable(RDuels plugin) {
    }

    @Override
    public void disable(RDuels plugin) {
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}

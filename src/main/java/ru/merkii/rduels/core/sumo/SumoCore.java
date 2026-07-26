package ru.merkii.rduels.core.sumo;

import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;

/**
 * Sumo module: knock-out win condition on sumo-flagged arenas.
 * Behaviour lives in the auto-registered listener.
 */
@Singleton
public class SumoCore implements Core {

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

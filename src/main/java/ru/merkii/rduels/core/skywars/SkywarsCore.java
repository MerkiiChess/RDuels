package ru.merkii.rduels.core.skywars;

import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;

/**
 * Skywars module: random loot chests on skywars-flagged arenas.
 * Behaviour lives in the auto-registered listener.
 */
@Singleton
public class SkywarsCore implements Core {

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

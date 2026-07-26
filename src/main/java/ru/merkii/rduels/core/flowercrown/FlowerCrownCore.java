package ru.merkii.rduels.core.flowercrown;

import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;

/**
 * Flower Crown module: steal-the-flowers objective on flower-crown-flagged arenas.
 * Logic lives in the auto-registered listener.
 */
@Singleton
public class FlowerCrownCore implements Core {

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

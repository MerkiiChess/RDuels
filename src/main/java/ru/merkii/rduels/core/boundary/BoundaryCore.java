package ru.merkii.rduels.core.boundary;

import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;

/**
 * Boundary/void module: keeps fighters inside the arena and out of the void.
 * Behaviour lives in the auto-registered listener.
 */
@Singleton
public class BoundaryCore implements Core {

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

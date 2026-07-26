package ru.merkii.rduels.core.goldenhead;

import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;

/**
 * Golden-head module: enhanced healing item applied on consume.
 * Behaviour lives in the auto-registered listener.
 */
@Singleton
public class GoldenHeadCore implements Core {

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

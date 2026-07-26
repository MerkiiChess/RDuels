package ru.merkii.rduels.core.killmessage;

import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;

/**
 * Kill-message module: cause-specific death messages for duel participants.
 * Behaviour lives entirely in the auto-registered listener.
 */
@Singleton
public class KillMessageCore implements Core {

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

package ru.merkii.rduels.core.elo;

import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;

/**
 * Rating module: Elo movement on fight end, reward tiers, rating placeholders.
 * The listener is auto-registered by the bootstrap; the config is proxy-backed,
 * so there is nothing to do on enable/reload.
 */
@Singleton
public class EloCore implements Core {

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

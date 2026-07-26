package ru.merkii.rduels.core.economy;

import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;

/**
 * Economy module: Vault-backed party creation cost and win rewards.
 * The reward listener is auto-registered; party cost is enforced in PartyCommand.
 */
@Singleton
public class EconomyCore implements Core {

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

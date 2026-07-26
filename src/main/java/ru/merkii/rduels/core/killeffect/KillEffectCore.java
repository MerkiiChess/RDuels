package ru.merkii.rduels.core.killeffect;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.killeffect.command.KillEffectCommand;

/**
 * Kill-effect module: /killeffect menu, per-player selected effect (persisted through
 * the statistic cache) and the listener that plays it on kills.
 */
@Singleton
public class KillEffectCore implements Core {

    private final Lamp<BukkitCommandActor> lamp;
    private final KillEffectCommand command;

    @Inject
    public KillEffectCore(Lamp<BukkitCommandActor> lamp, KillEffectCommand command) {
        this.lamp = lamp;
        this.command = command;
    }

    @Override
    public void enable(RDuels plugin) {
        lamp.register(command);
    }

    @Override
    public void disable(RDuels plugin) {
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}

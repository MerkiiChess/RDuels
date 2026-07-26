package ru.merkii.rduels.core.rematch;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.rematch.command.RematchCommand;

/**
 * Rematch module: /rematch re-challenges the last 1v1 opponent with the same kit.
 * The last opponent/kit is recorded by the auto-registered listener.
 */
@Singleton
public class RematchCore implements Core {

    private final Lamp<BukkitCommandActor> lamp;
    private final RematchCommand command;

    @Inject
    public RematchCore(Lamp<BukkitCommandActor> lamp, RematchCommand command) {
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

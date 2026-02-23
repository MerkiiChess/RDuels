package ru.merkii.rduels.core.customkit;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.customkit.command.CustomKitCommand;
import ru.merkii.rduels.core.customkit.listener.CustomKitListener;

@Getter
@Singleton
public class CustomKitCore implements Core {

    private final Lamp<BukkitCommandActor> lamp;

    @Inject
    public CustomKitCore(Lamp<BukkitCommandActor> lamp) {
        this.lamp = lamp;
    }

    @Override
    public void enable(RDuels plugin) {
        plugin.registerListeners(CustomKitListener.class);
        lamp.register(RDuels.beanScope().get(CustomKitCommand.class));
    }

    @Override
    public void disable(RDuels plugin) {
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}

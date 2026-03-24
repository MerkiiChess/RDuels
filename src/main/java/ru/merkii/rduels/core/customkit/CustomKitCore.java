package ru.merkii.rduels.core.customkit;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.customkit.command.CustomKitCommand;
import ru.merkii.rduels.core.customkit.listener.CustomKitListener;

@Getter
@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CustomKitCore implements Core {

    Lamp<BukkitCommandActor> lamp;

    @Override
    public void enable(RDuels plugin) {
        lamp.register(RDuels.beanScope().get(CustomKitCommand.class));
    }

    @Override
    public void disable(RDuels plugin) {
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}

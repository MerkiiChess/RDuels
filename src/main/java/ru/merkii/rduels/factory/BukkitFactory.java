package ru.merkii.rduels.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.lamp.exception.CustomExceptionHandler;
import ru.merkii.rduels.lamp.parameter.DuelPlayerParameterType;

@Factory
public class BukkitFactory {

    @Bean
    public Lamp<BukkitCommandActor> bukkitCommandActorLamp(RDuels plugin, MessageConfig messageConfig, DuelPlayerParameterType duelPlayerParameterType) {
        return BukkitLamp.builder(plugin)
                .parameterTypes(builder -> {
                    builder.addParameterType(DuelPlayer.class, duelPlayerParameterType);
                })
                .exceptionHandler(new CustomExceptionHandler(messageConfig))
                .build();
    }

}

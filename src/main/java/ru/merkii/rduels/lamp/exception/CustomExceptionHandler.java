package ru.merkii.rduels.lamp.exception;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.exception.NoPermissionException;
import ru.merkii.rduels.config.messages.MessageConfig;

public class CustomExceptionHandler extends BukkitExceptionHandler {

    private final MessageConfig messageConfig;

    public CustomExceptionHandler(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    @Override
    public void onNoPermission(@NotNull NoPermissionException e, @NotNull BukkitCommandActor actor) {
        messageConfig.sendTo(actor.requirePlayer(), "no-permission");
    }

    @HandleException
    public void duelPlayerNotFound(@NotNull DuelPlayerException e, @NotNull BukkitCommandActor actor) {
        messageConfig.sendTo(actor.requirePlayer(), "duel-offline");
    }
}

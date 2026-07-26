package ru.merkii.rduels.core.playersetting;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.playersetting.command.SettingsCommand;

/**
 * Player preference module: /settings menu with the scoreboard, duel-request and
 * auto-GG toggles. The toggles themselves persist through the statistic cache.
 */
@Singleton
public class PlayerSettingsCore implements Core {

    private final Lamp<BukkitCommandActor> lamp;
    private final SettingsCommand settingsCommand;

    @Inject
    public PlayerSettingsCore(Lamp<BukkitCommandActor> lamp, SettingsCommand settingsCommand) {
        this.lamp = lamp;
        this.settingsCommand = settingsCommand;
    }

    @Override
    public void enable(RDuels plugin) {
        lamp.register(settingsCommand);
    }

    @Override
    public void disable(RDuels plugin) {
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}

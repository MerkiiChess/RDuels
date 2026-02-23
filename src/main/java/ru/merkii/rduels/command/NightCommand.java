package ru.merkii.rduels.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.manager.DatabaseManager;

@Singleton
public class NightCommand {

    private final DatabaseManager databaseManager;
    private final SettingsConfiguration settingsConfiguration;
    private final MessageConfig config;

    @Inject
    public NightCommand(DatabaseManager databaseManager, SettingsConfiguration settingsConfiguration, MessageConfig config) {
        this.databaseManager = databaseManager;
        this.settingsConfiguration = settingsConfiguration;
        this.config = config;
    }

    @Command("night")
    public void onNight(Player player) {
        databaseManager.setNight(player.getUniqueId()).join();
        player.setPlayerTime(settingsConfiguration.nightTicks(), false);
        config.sendTo(player, "night");
    }

}

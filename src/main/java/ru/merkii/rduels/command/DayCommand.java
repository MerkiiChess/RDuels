package ru.merkii.rduels.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.manager.DatabaseManager;

@Singleton
public class DayCommand {

    private final DatabaseManager databaseManager;
    private final SettingsConfiguration settingsConfiguration;
    private final MessageConfig config;

    @Inject
    public DayCommand(DatabaseManager databaseManager, SettingsConfiguration settingsConfiguration, MessageConfig config) {
        this.databaseManager = databaseManager;
        this.settingsConfiguration = settingsConfiguration;
        this.config = config;
    }

    @Command("day")
    public void onDay(Player player) {
        databaseManager.setDay(player.getUniqueId()).join();
        player.setPlayerTime(settingsConfiguration.dayTicks(), false);
        config.sendTo(player, "day");
    }

}

package ru.merkii.rduels.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.manager.DatabaseManager;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NightCommand {

    DatabaseManager databaseManager;
    SettingsConfiguration settingsConfiguration;
    MessageConfig config;

    @Command("night")
    public void onNight(Player player) {
        databaseManager.setNight(player.getUniqueId());
        player.setPlayerTime(settingsConfiguration.nightTicks(), false);
        config.sendTo(player, "night");
    }

}

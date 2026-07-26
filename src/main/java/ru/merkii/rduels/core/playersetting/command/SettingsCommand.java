package ru.merkii.rduels.core.playersetting.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.core.playersetting.menu.PlayerSettingsMenu;

@Singleton
public class SettingsCommand {

    @Inject
    public SettingsCommand() {
    }

    @Command({"settings", "duelsettings"})
    public void onSettings(Player player) {
        new PlayerSettingsMenu().open(player);
    }

}

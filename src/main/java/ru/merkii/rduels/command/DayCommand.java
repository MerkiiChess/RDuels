package ru.merkii.rduels.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.statistic.StatisticService;

@Singleton
public class DayCommand {

    private final StatisticService statisticService;
    private final SettingsConfiguration settingsConfiguration;
    private final MessageConfig config;

    @Inject
    public DayCommand(StatisticService statisticService, SettingsConfiguration settingsConfiguration, MessageConfig config) {
        this.statisticService = statisticService;
        this.settingsConfiguration = settingsConfiguration;
        this.config = config;
    }

    @Command("day")
    public void onDay(Player player) {
        statisticService.setDay(player.getUniqueId());
        player.setPlayerTime(settingsConfiguration.dayTicks(), false);
        config.sendTo(player, "day");
    }

}

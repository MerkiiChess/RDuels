package ru.merkii.rduels.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.statistic.StatisticService;

@Singleton
public class NightCommand {

    private final StatisticService statisticService;
    private final SettingsConfiguration settingsConfiguration;
    private final MessageConfig config;

    @Inject
    public NightCommand(StatisticService statisticService, SettingsConfiguration settingsConfiguration, MessageConfig config) {
        this.statisticService = statisticService;
        this.settingsConfiguration = settingsConfiguration;
        this.config = config;
    }

    @Command("night")
    public void onNight(Player player) {
        statisticService.setNight(player.getUniqueId());
        player.setPlayerTime(settingsConfiguration.nightTicks(), false);
        config.sendTo(player, "night");
    }

}

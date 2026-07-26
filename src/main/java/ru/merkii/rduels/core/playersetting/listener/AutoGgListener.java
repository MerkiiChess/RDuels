package ru.merkii.rduels.core.playersetting.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.statistic.StatisticService;

import java.util.Arrays;
import java.util.Objects;
import java.util.List;

/**
 * Sends the automatic "gg" line on behalf of players who enabled the Auto-GG toggle
 * when their fight ends.
 */
@Singleton
public class AutoGgListener implements Listener {

    private final StatisticService statisticService;
    private final MessageConfig messages;

    @Inject
    public AutoGgListener(StatisticService statisticService, MessageConfig messages) {
        this.statisticService = statisticService;
        this.messages = messages;
    }

    @EventHandler
    public void onStopFight(DuelStopFightEvent event) {
        List<Player> participants = Arrays.asList(event.getWinner(), event.getLoser());
        participants.stream()
                .filter(Objects::nonNull)
                .filter(player -> statisticService.isAutoGg(player.getUniqueId()))
                .forEach(sender -> {
                    Component message = messages.message(
                            Placeholder.wrapped("(player)", sender.getName()), "auto-gg-format");
                    participants.stream().filter(Objects::nonNull).forEach(target -> target.sendMessage(message));
                });
    }

}

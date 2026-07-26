package ru.merkii.rduels.gui.extractor;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;
import ru.merkii.rduels.statistic.StatisticService;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolves the %setting_*% placeholders of the player-settings menu into the on/off
 * state strings. Requires the opening code to put "player" into the menu context.
 */
public class PlayerSettingValueExtractor implements ValueExtractor {

    private final StatisticService statisticService;
    private final MenuConfiguration config;

    public PlayerSettingValueExtractor(StatisticService statisticService, MenuConfiguration config) {
        this.statisticService = statisticService;
        this.config = config;
    }

    @Override
    public String extract(InventoryContext context, String text, Object model) {
        if (!text.contains("%setting_")) {
            return text;
        }
        Optional<Player> player = context.get("player");
        if (player.isEmpty()) {
            return text;
        }
        UUID uuid = player.get().getUniqueId();
        return text
                .replace("%setting_scoreboard%", state(statisticService.isScoreboardEnabled(uuid)))
                .replace("%setting_duel_requests%", state(statisticService.isDuelRequestsEnabled(uuid)))
                .replace("%setting_auto_gg%", state(statisticService.isAutoGg(uuid)))
                .replace("%setting_auto_requeue%", state(statisticService.isAutoRequeue(uuid)));
    }

    private String state(boolean enabled) {
        return config.messages().plainMessage(enabled ? "setting-on" : "setting-off");
    }

}

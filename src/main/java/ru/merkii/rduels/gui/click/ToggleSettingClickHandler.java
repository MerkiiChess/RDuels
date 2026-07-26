package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.core.playersetting.menu.PlayerSettingsMenu;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.statistic.StatisticService;

import java.util.UUID;

/**
 * Toggles the player preference referenced by the clicked item's {@code setting-id}
 * (scoreboard / duel-requests / auto-gg) and re-renders the settings menu.
 */
public class ToggleSettingClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "TOGGLE_SETTING";

    private final StatisticService statisticService;
    private final MenuConfiguration config;

    public ToggleSettingClickHandler(StatisticService statisticService, MenuConfiguration config) {
        this.statisticService = statisticService;
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        InventoryItem itemConfig = handler.requireArg(ClickHandler.ITEM_CONFIG);
        String settingId = itemConfig.root().node("setting-id").getString();
        if (settingId == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        switch (settingId) {
            case "scoreboard" -> statisticService.setScoreboardEnabled(uuid, !statisticService.isScoreboardEnabled(uuid));
            case "duel-requests" -> statisticService.setDuelRequestsEnabled(uuid, !statisticService.isDuelRequestsEnabled(uuid));
            case "auto-gg" -> statisticService.setAutoGg(uuid, !statisticService.isAutoGg(uuid));
            case "auto-requeue" -> statisticService.setAutoRequeue(uuid, !statisticService.isAutoRequeue(uuid));
            default -> {
                return;
            }
        }
        config.settings().notification().playSound(player, "select-option");
        // Re-open so the state placeholders in names/lore re-render with the new values.
        new PlayerSettingsMenu().open(player);
    }
}

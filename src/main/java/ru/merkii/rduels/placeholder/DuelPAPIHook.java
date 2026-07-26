package ru.merkii.rduels.placeholder;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.serializer.ComponentSerializerProviders;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.elo.api.EloAPI;
import ru.merkii.rduels.statistic.StatisticService;
import ru.merkii.rduels.util.TimeUtil;

@Singleton
public class DuelPAPIHook extends PlaceholderExpansion {

    private transient final StatisticService statisticService;
    private transient final MessageConfig messageConfig;
    private transient final DuelAPI duelAPI;
    private transient final EloAPI eloAPI;

    @Inject
    public DuelPAPIHook(StatisticService statisticService, MessageConfig messageConfig, DuelAPI duelAPI, EloAPI eloAPI) {
        this.statisticService = statisticService;
        this.messageConfig = messageConfig;
        this.duelAPI = duelAPI;
        this.eloAPI = eloAPI;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String nullPlaceholder = ComponentSerializerProviders.MINI_MESSAGE.componentSerializer().serialize(messageConfig.message("null-placeholder"));
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        return switch (identifier) {
            case "kills" -> String.valueOf(this.statisticService.getKills(player.getUniqueId()));
            case "death" -> String.valueOf(this.statisticService.getDeaths(player.getUniqueId()));
            case "wins" -> String.valueOf(this.statisticService.getWinRounds(player.getUniqueId()));
            case "all_rounds" ->
                    String.valueOf(this.statisticService.getAllRounds(player.getUniqueId()) - this.statisticService.getWinRounds(player.getUniqueId()));
            case "elo" -> String.valueOf(this.statisticService.getElo(player.getUniqueId()));
            case "tier" -> ComponentSerializerProviders.LEGACY_SECTION.componentSerializer()
                    .serialize(MessageConfig.serializer.deserialize(this.eloAPI.getTierName(duelPlayer)));
            case "match_wins" -> String.valueOf(this.statisticService.getWins(player.getUniqueId()));
            case "match_losses" -> String.valueOf(this.statisticService.getLosses(player.getUniqueId()));
            case "opponent" ->
                    player == null || !duelAPI.isFightPlayer(duelPlayer) ? nullPlaceholder : duelAPI.getOpponentFromFight(duelPlayer).getName();
            case "time" ->
                    player == null || !duelAPI.isFightPlayer(duelPlayer) ? nullPlaceholder : TimeUtil.getTimeInMaxUnit(duelAPI.getFightModelFromPlayer(duelPlayer).getBukkitTask().getTime() * 1000L);
            case "count_rounds" ->
                    player == null || !duelAPI.isFightPlayer(duelPlayer) ? nullPlaceholder : String.valueOf(duelAPI.getFightModelFromPlayer(duelPlayer).getNumGames());
            case "played_count_rounds" ->
                    player == null || !duelAPI.isFightPlayer(duelPlayer) ? nullPlaceholder : String.valueOf(duelAPI.getFightModelFromPlayer(duelPlayer).getCountNumGames());
            case "kit" ->
                    player == null || !duelAPI.isFightPlayer(duelPlayer) ? nullPlaceholder : duelAPI.getFightModelFromPlayer(duelPlayer).getKitModel().getDisplayName();
            default -> null;
        };
    }


    @Override
    public String getAuthor() {
        return "RuMerkii";
    }

    @Override
    public String getIdentifier() {
        return "duel";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

}
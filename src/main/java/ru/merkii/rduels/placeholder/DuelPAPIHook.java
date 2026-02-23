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
import ru.merkii.rduels.manager.DatabaseManager;
import ru.merkii.rduels.util.TimeUtil;

@Singleton
public class DuelPAPIHook extends PlaceholderExpansion {

    private transient final DatabaseManager databaseManager;
    private transient final MessageConfig messageConfig;
    private transient final DuelAPI duelAPI;

    @Inject
    public DuelPAPIHook(DatabaseManager databaseManager, MessageConfig messageConfig, DuelAPI duelAPI) {
        this.databaseManager = databaseManager;
        this.messageConfig = messageConfig;
        this.duelAPI = duelAPI;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String nullPlaceholder = ComponentSerializerProviders.MINI_MESSAGE.componentSerializer().serialize(messageConfig.message("null-placeholder"));
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        return switch (identifier) {
            case "kills" -> String.valueOf(this.databaseManager.getKills(player.getUniqueId()).join());
            case "death" -> String.valueOf(this.databaseManager.getDeaths(player.getUniqueId()).join());
            case "wins" -> String.valueOf(this.databaseManager.getWinRounds(player.getUniqueId()).join());
            case "all_rounds" ->
                    String.valueOf(this.databaseManager.getAllRounds(player.getUniqueId()).join() - this.databaseManager.getWinRounds(duelPlayer.getUUID()).join());
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
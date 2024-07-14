package ru.merkii.rduels.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.manager.DatabaseManager;
import ru.merkii.rduels.util.TimeUtil;

public class DuelPAPIHook extends PlaceholderExpansion {

    private transient final RDuels plugin = RDuels.getInstance();
    private transient final DatabaseManager databaseManager = plugin.getDatabaseManager();

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        DuelAPI duelAPI = DuelCore.INSTANCE.getDuelAPI();
        return switch (identifier) {
            case "kills" -> String.valueOf(this.databaseManager.getKills(player).join());
            case "death" -> String.valueOf(this.databaseManager.getDeaths(player).join());
            case "wins" -> String.valueOf(this.databaseManager.getWinRounds(player).join());
            case "all_rounds" ->
                    String.valueOf(this.databaseManager.getAllRounds(player).join() - this.databaseManager.getWinRounds(player).join());
            case "opponent" ->
                    player == null || !duelAPI.isFightPlayer(player) ? this.plugin.getPluginMessage().getMessage("nullPlaceholder") : duelAPI.getOpponentFromFight(player).getName();
            case "time" ->
                    player == null || !duelAPI.isFightPlayer(player) ? this.plugin.getPluginMessage().getMessage("nullPlaceholder") : TimeUtil.getTimeInMaxUnit(duelAPI.getFightModelFromPlayer(player).getBukkitTask().getTime() * 1000L);
            case "count_rounds" ->
                    player == null || !duelAPI.isFightPlayer(player) ? this.plugin.getPluginMessage().getMessage("nullPlaceholder") : String.valueOf(duelAPI.getFightModelFromPlayer(player).getNumGames());
            case "played_count_rounds" ->
                    player == null || !duelAPI.isFightPlayer(player) ? this.plugin.getPluginMessage().getMessage("nullPlaceholder") : String.valueOf(duelAPI.getFightModelFromPlayer(player).getCountNumGames());
            case "kit" ->
                    player == null || !duelAPI.isFightPlayer(player) ? this.plugin.getPluginMessage().getMessage("nullPlaceholder") : duelAPI.getFightModelFromPlayer(player).getKitModel().getDisplayName();
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

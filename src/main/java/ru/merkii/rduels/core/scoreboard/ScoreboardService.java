package ru.merkii.rduels.core.scoreboard;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.elo.api.EloAPI;
import ru.merkii.rduels.core.scoreboard.config.ScoreboardConfiguration;
import ru.merkii.rduels.statistic.StatisticService;
import ru.merkii.rduels.util.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flicker-free sidebar built on per-player scoreboards with team prefixes.
 * Lines come from scoreboard.yml and support the plugin placeholders plus PAPI.
 */
@Singleton
public class ScoreboardService {

    private final ScoreboardConfiguration config;
    private final StatisticService statisticService;
    private final DuelAPI duelAPI;
    private final EloAPI eloAPI;
    private final Map<UUID, BoardHandle> boards = new ConcurrentHashMap<>();

    @Inject
    public ScoreboardService(ScoreboardConfiguration config, StatisticService statisticService, DuelAPI duelAPI, EloAPI eloAPI) {
        this.config = config;
        this.statisticService = statisticService;
        this.duelAPI = duelAPI;
        this.eloAPI = eloAPI;
    }

    /** Renders (or hides) the sidebar for the player according to config and their toggle. */
    public void update(Player player) {
        if (!config.enabled() || !statisticService.isScoreboardEnabled(player.getUniqueId())) {
            remove(player);
            return;
        }
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        DuelFightModel fightModel = duelAPI.getFightModelFromPlayer(duelPlayer);

        List<String> template = fightModel != null
                ? config.fightLines()
                : duelAPI.isSpectate(duelPlayer) ? config.spectatorLines() : config.lobbyLines();

        BoardHandle handle = boards.computeIfAbsent(player.getUniqueId(), uuid -> new BoardHandle());
        handle.render(player,
                MiniMessage.miniMessage().deserialize(replacePlaceholders(config.title(), player, duelPlayer, fightModel)),
                template.stream()
                        .map(line -> replacePlaceholders(line, player, duelPlayer, fightModel))
                        .map(line -> (Component) MiniMessage.miniMessage().deserialize(line))
                        .toList());
    }

    public void remove(Player player) {
        BoardHandle handle = boards.remove(player.getUniqueId());
        if (handle != null && player.isOnline()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    public void removeAll() {
        Bukkit.getOnlinePlayers().forEach(this::remove);
        boards.clear();
    }

    private String replacePlaceholders(String line, Player player, DuelPlayer duelPlayer, DuelFightModel fightModel) {
        UUID uuid = player.getUniqueId();
        String result = line
                .replace("(player)", player.getName())
                .replace("(elo)", String.valueOf(statisticService.getElo(uuid)))
                .replace("(tier)", eloAPI.getTierName(duelPlayer))
                .replace("(kills)", String.valueOf(statisticService.getKills(uuid)))
                .replace("(deaths)", String.valueOf(statisticService.getDeaths(uuid)))
                .replace("(wins)", String.valueOf(statisticService.getWins(uuid)))
                .replace("(losses)", String.valueOf(statisticService.getLosses(uuid)))
                .replace("(win_rounds)", String.valueOf(statisticService.getWinRounds(uuid)))
                .replace("(all_rounds)", String.valueOf(statisticService.getAllRounds(uuid)))
                .replace("(online)", String.valueOf(Bukkit.getOnlinePlayers().size()));

        if (fightModel != null) {
            DuelPlayer opponent = duelAPI.getOpponentFromFight(fightModel, duelPlayer);
            result = result
                    .replace("(opponent)", opponent == null ? "-" : opponent.getName())
                    .replace("(kit)", fightModel.getKitModel() == null ? "-" : fightModel.getKitModel().getDisplayName())
                    .replace("(round)", String.valueOf(fightModel.getCountNumGames() + 1))
                    .replace("(rounds)", String.valueOf(fightModel.getNumGames()))
                    .replace("(time)", fightModel.getBukkitTask() == null
                            ? "-"
                            : TimeUtil.getTimeInMaxUnit(fightModel.getBukkitTask().getTime() * 1000L));
        }

        return PlaceholderAPI.setPlaceholders(player, result);
    }

    /** One player's sidebar: 15 pre-registered teams whose prefixes carry the line text. */
    private static final class BoardHandle {

        private static final int MAX_LINES = 15;

        private final Scoreboard board;
        private final Objective objective;
        private final Team[] teams = new Team[MAX_LINES];
        private final String[] entries = new String[MAX_LINES];
        private int visibleLines;

        private BoardHandle() {
            this.board = Bukkit.getScoreboardManager().getNewScoreboard();
            this.objective = board.registerNewObjective("rduels", Criteria.DUMMY, Component.empty());
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            for (int i = 0; i < MAX_LINES; i++) {
                entries[i] = ChatColor.values()[i].toString() + ChatColor.RESET;
                teams[i] = board.registerNewTeam("rduels-line-" + i);
                teams[i].addEntry(entries[i]);
            }
        }

        private void render(Player player, Component title, List<Component> lines) {
            objective.displayName(title);
            int count = Math.min(lines.size(), MAX_LINES);
            for (int i = 0; i < count; i++) {
                teams[i].prefix(lines.get(i));
                objective.getScore(entries[i]).setScore(count - i);
            }
            // Hide lines that disappeared since the previous render (e.g. fight → lobby).
            for (int i = count; i < visibleLines; i++) {
                board.resetScores(entries[i]);
            }
            visibleLines = count;
            if (player.getScoreboard() != board) {
                player.setScoreboard(board);
            }
        }
    }

    /** Placeholder map hook point (kept package-private for tests). */
    Map<UUID, BoardHandle> boards() {
        return new HashMap<>(boards);
    }

}

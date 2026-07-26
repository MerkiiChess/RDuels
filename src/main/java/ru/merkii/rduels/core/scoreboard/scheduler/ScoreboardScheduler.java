package ru.merkii.rduels.core.scoreboard.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.scoreboard.ScoreboardService;
import ru.merkii.rduels.core.scoreboard.config.ScoreboardConfiguration;

public class ScoreboardScheduler extends BukkitRunnable {

    private final ScoreboardService scoreboardService;

    private ScoreboardScheduler(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    public static ScoreboardScheduler start(RDuels plugin, ScoreboardService service, ScoreboardConfiguration config) {
        ScoreboardScheduler scheduler = new ScoreboardScheduler(service);
        long interval = Math.max(1, config.updateIntervalTicks());
        scheduler.runTaskTimer(plugin, interval, interval);
        return scheduler;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(scoreboardService::update);
    }
}

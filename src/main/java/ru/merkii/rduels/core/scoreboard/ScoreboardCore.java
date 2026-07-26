package ru.merkii.rduels.core.scoreboard;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.scoreboard.config.ScoreboardConfiguration;
import ru.merkii.rduels.core.scoreboard.scheduler.ScoreboardScheduler;

@Singleton
public class ScoreboardCore implements Core {

    private final ScoreboardService scoreboardService;
    private final ScoreboardConfiguration config;
    private ScoreboardScheduler scheduler;

    @Inject
    public ScoreboardCore(ScoreboardService scoreboardService, ScoreboardConfiguration config) {
        this.scoreboardService = scoreboardService;
        this.config = config;
    }

    @Override
    public void enable(RDuels plugin) {
        this.scheduler = ScoreboardScheduler.start(plugin, scoreboardService, config);
    }

    @Override
    public void disable(RDuels plugin) {
        if (this.scheduler != null) {
            this.scheduler.cancel();
        }
        this.scoreboardService.removeAll();
    }

    @Override
    public void reloadConfig(RDuels plugin) {
        // Config is proxy-backed; the next scheduler tick picks up new lines automatically.
    }
}

package ru.merkii.rduels.core.scoreboard.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.merkii.rduels.core.scoreboard.ScoreboardService;

@Singleton
public class ScoreboardListener implements Listener {

    private final ScoreboardService scoreboardService;

    @Inject
    public ScoreboardListener(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        scoreboardService.remove(event.getPlayer());
    }

}

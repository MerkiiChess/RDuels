package ru.merkii.rduels.core.scoreboard.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface ScoreboardConfiguration {

    boolean enabled();

    /** Refresh period of the sidebar, in ticks. */
    int updateIntervalTicks();

    /** MiniMessage title of the sidebar. */
    String title();

    /** Lines shown outside of fights. */
    List<String> lobbyLines();

    /** Lines shown while the player is in a fight. */
    List<String> fightLines();

    /** Lines shown while the player spectates a fight. */
    List<String> spectatorLines();

}

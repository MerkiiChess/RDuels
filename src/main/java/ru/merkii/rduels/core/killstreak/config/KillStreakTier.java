package ru.merkii.rduels.core.killstreak.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.config.model.ExecuteCommand;

import java.util.List;

/** A kill-streak milestone: announced and rewarded when a player reaches {@link #count()} kills. */
@ConfigInterface
public interface KillStreakTier {

    /** Consecutive kills required to trigger this milestone. */
    int count();

    /** Announcement (MiniMessage); placeholders (player) and (streak). */
    String message();

    /** Reward commands run once when the streak is reached ([console]/[player] + PAPI). */
    default List<ExecuteCommand> commands() {
        return List.of();
    }

}

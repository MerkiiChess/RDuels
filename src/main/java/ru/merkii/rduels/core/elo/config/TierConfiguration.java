package ru.merkii.rduels.core.elo.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.config.model.ExecuteCommand;

import java.util.List;

/**
 * One reward tier: reached when the player's Elo hits {@link #elo()}. The reward
 * commands run once, the first time the tier is reached (tracked per player in the DB).
 */
@ConfigInterface
public interface TierConfiguration {

    /** Display name of the tier (MiniMessage). */
    String name();

    /** Minimum Elo required to reach this tier. */
    int elo();

    /** Commands executed once on reaching the tier ([console]/[player] prefixes, PAPI placeholders). */
    List<ExecuteCommand> commands();

}

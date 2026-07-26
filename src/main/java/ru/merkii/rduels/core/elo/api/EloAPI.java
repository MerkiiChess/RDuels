package ru.merkii.rduels.core.elo.api;

import ru.merkii.rduels.adapter.DuelPlayer;

/**
 * Public rating API. Fetch via {@code RDuels.beanScope().get(EloAPI.class)}.
 */
public interface EloAPI {

    /** Current Elo rating of the player. */
    int getElo(DuelPlayer player);

    /** MiniMessage display name of the tier matching the player's current Elo, or an empty string. */
    String getTierName(DuelPlayer player);

    /** Rating points the winner would gain (and the loser lose, before the floor) for these ratings. */
    int calculateDelta(int winnerElo, int loserElo);

    /**
     * Applies a match result: moves Elo between the two players, sends the rating
     * messages and grants any newly reached tier rewards. Call on the main thread.
     */
    void applyMatchResult(DuelPlayer winner, DuelPlayer loser);

}

package ru.merkii.rduels.core.elo.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface EloConfiguration {

    /** Master switch for the whole rating system. */
    boolean enabled();

    /** If true, Elo changes apply only to ranked (queue) fights; unranked 1v1s stay rating-free. */
    boolean onlyRanked();

    /** K-factor of the Elo formula — the maximum rating swing per game. */
    int kFactor();

    /** Ratings never drop below this floor. */
    int minElo();

    /** Reward tiers, ordered by their {@code elo} threshold ascending. */
    List<TierConfiguration> tiers();

}

package ru.merkii.rduels.core.elo;

import org.junit.jupiter.api.Test;
import ru.merkii.rduels.core.elo.api.provider.EloAPIProvider;
import ru.merkii.rduels.core.elo.config.EloConfiguration;
import ru.merkii.rduels.core.elo.config.TierConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure-math tests for the Elo delta formula (no Bukkit involved).
 */
class EloDeltaTest {

    private EloAPIProvider provider(int kFactor) {
        EloConfiguration config = new EloConfiguration() {
            @Override public boolean enabled() { return true; }
            @Override public boolean onlyRanked() { return false; }
            @Override public int kFactor() { return kFactor; }
            @Override public int minElo() { return 0; }
            @Override public List<TierConfiguration> tiers() { return List.of(); }
        };
        // calculateDelta uses only the config; the other collaborators are never touched.
        return new EloAPIProvider(null, config, null);
    }

    @Test
    void equalRatingsSplitTheKFactor() {
        assertEquals(16, provider(32).calculateDelta(1000, 1000));
    }

    @Test
    void favouredWinnerGainsLess() {
        int delta = provider(32).calculateDelta(1400, 1000);
        assertTrue(delta < 16, "фаворит должен получать меньше 16, а получил " + delta);
        assertTrue(delta >= 1, "изменение не может быть меньше 1");
    }

    @Test
    void upsetWinnerGainsMore() {
        int delta = provider(32).calculateDelta(1000, 1400);
        assertTrue(delta > 16, "андердог должен получать больше 16, а получил " + delta);
    }

    @Test
    void deltaNeverDropsBelowOne() {
        assertEquals(1, provider(32).calculateDelta(3000, 0));
    }

    @Test
    void kFactorScalesTheDelta() {
        assertEquals(8, provider(16).calculateDelta(1000, 1000));
    }
}

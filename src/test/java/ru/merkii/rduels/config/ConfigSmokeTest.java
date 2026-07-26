package ru.merkii.rduels.config;

import org.junit.jupiter.api.Test;
import ru.merkii.rduels.core.bedwars.config.BedwarsConfiguration;
import ru.merkii.rduels.core.bedwars.config.BedwarsShopConfiguration;
import ru.merkii.rduels.core.bedwars.config.BedwarsUpgradeConfiguration;
import ru.merkii.rduels.core.boundary.config.BoundaryConfiguration;
import ru.merkii.rduels.core.flowercrown.config.FlowerCrownConfiguration;
import ru.merkii.rduels.core.tnttag.config.TntTagConfiguration;
import ru.merkii.rduels.core.economy.config.EconomyConfiguration;
import ru.merkii.rduels.core.elo.config.EloConfiguration;
import ru.merkii.rduels.core.goldenhead.config.GoldenHeadConfiguration;
import ru.merkii.rduels.core.killeffect.config.KillEffectConfiguration;
import ru.merkii.rduels.core.killmessage.config.KillMessageConfiguration;
import ru.merkii.rduels.core.killstreak.config.KillStreakConfiguration;
import ru.merkii.rduels.core.queue.config.QueueConfiguration;
import ru.merkii.rduels.core.randomkit.config.RandomKitConfiguration;
import ru.merkii.rduels.core.scoreboard.config.ScoreboardConfiguration;
import ru.merkii.rduels.core.skywars.config.SkywarsConfiguration;
import ru.merkii.rduels.core.sumo.config.SumoConfiguration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies every new config resource shipped in resources/ parses cleanly into its
 * config interface through the real serializer pipeline.
 */
class ConfigSmokeTest {

    @Test
    void eloConfigParses() throws Exception {
        EloConfiguration config = ConfigTestSupport.load("elo.yml", EloConfiguration.class);
        assertNotNull(config);
        assertTrue(config.kFactor() > 0, "k-factor должен быть положительным");
        assertFalse(config.tiers().isEmpty(), "должны быть тиры");
        assertNotNull(config.tiers().get(0).name());
    }

    @Test
    void queueConfigParses() throws Exception {
        QueueConfiguration config = ConfigTestSupport.load("queue.yml", QueueConfiguration.class);
        assertNotNull(config);
        assertTrue(config.matchIntervalTicks() > 0);
        assertTrue(config.numGames() >= 1);
    }

    @Test
    void scoreboardConfigParses() throws Exception {
        ScoreboardConfiguration config = ConfigTestSupport.load("scoreboard.yml", ScoreboardConfiguration.class);
        assertNotNull(config);
        assertNotNull(config.title());
        assertFalse(config.lobbyLines().isEmpty());
        assertFalse(config.fightLines().isEmpty());
    }

    @Test
    void killEffectsConfigParses() throws Exception {
        KillEffectConfiguration config = ConfigTestSupport.load("kill-effects.yml", KillEffectConfiguration.class);
        assertNotNull(config);
        assertFalse(config.effects().isEmpty());
        assertNotNull(config.effects().get(0).id());
        assertNotNull(config.effects().get(0).material());
    }

    @Test
    void killStreaksConfigParses() throws Exception {
        KillStreakConfiguration config = ConfigTestSupport.load("kill-streaks.yml", KillStreakConfiguration.class);
        assertNotNull(config);
        assertFalse(config.tiers().isEmpty());
        assertTrue(config.tiers().get(0).count() > 0);
    }

    @Test
    void killMessagesConfigParses() throws Exception {
        KillMessageConfiguration config = ConfigTestSupport.load("kill-messages.yml", KillMessageConfiguration.class);
        assertNotNull(config);
        assertNotNull(config.defaultMessage());
        assertNotNull(config.environmentName());
        assertTrue(config.causes().containsKey("void"));
    }

    @Test
    void goldenHeadConfigParses() throws Exception {
        GoldenHeadConfiguration config = ConfigTestSupport.load("golden-head.yml", GoldenHeadConfiguration.class);
        assertNotNull(config);
        assertNotNull(config.material());
        assertFalse(config.effects().isEmpty());
    }

    @Test
    void boundaryConfigParses() throws Exception {
        BoundaryConfiguration config = ConfigTestSupport.load("boundary.yml", BoundaryConfiguration.class);
        assertNotNull(config);
        assertNotNull(config.voidAction());
        assertNotNull(config.warnMessage());
    }

    @Test
    void sumoConfigParses() throws Exception {
        SumoConfiguration config = ConfigTestSupport.load("sumo.yml", SumoConfiguration.class);
        assertNotNull(config);
        assertTrue(config.fallDistance() > 0);
    }

    @Test
    void economyConfigParses() throws Exception {
        EconomyConfiguration config = ConfigTestSupport.load("economy.yml", EconomyConfiguration.class);
        assertNotNull(config);
        assertTrue(config.partyCost() >= 0);
        assertTrue(config.winReward() >= 0);
    }

    @Test
    void randomKitsConfigParses() throws Exception {
        RandomKitConfiguration config = ConfigTestSupport.load("random-kits.yml", RandomKitConfiguration.class);
        assertNotNull(config);
        assertNotNull(config.include());
        assertNotNull(config.exclude());
    }

    @Test
    void skywarsLootConfigParses() throws Exception {
        SkywarsConfiguration config = ConfigTestSupport.load("skywars-loot.yml", SkywarsConfiguration.class);
        assertNotNull(config);
        assertTrue(config.searchRadius() > 0);
        assertFalse(config.loot().isEmpty());
        assertNotNull(config.loot().get(0).material());
    }

    @Test
    void bedwarsConfigParses() throws Exception {
        BedwarsConfiguration config = ConfigTestSupport.load("bedwars.yml", BedwarsConfiguration.class);
        assertNotNull(config);
        assertTrue(config.respawnSeconds() > 0);
        assertFalse(config.generators().isEmpty());
        assertNotNull(config.generators().get("IRON_BLOCK"));
    }

    @Test
    void bedwarsShopConfigParses() throws Exception {
        BedwarsShopConfiguration config = ConfigTestSupport.load("bedwars-shop.yml", BedwarsShopConfiguration.class);
        assertNotNull(config);
        assertFalse(config.items().isEmpty());
        assertNotNull(config.items().get(0).material());
        assertNotNull(config.items().get(0).costResource());
    }

    @Test
    void bedwarsUpgradesConfigParses() throws Exception {
        BedwarsUpgradeConfiguration config = ConfigTestSupport.load("bedwars-upgrades.yml", BedwarsUpgradeConfiguration.class);
        assertNotNull(config);
        assertFalse(config.upgrades().isEmpty());
        assertFalse(config.upgrades().get(0).levels().isEmpty());
    }

    @Test
    void tntTagConfigParses() throws Exception {
        TntTagConfiguration config = ConfigTestSupport.load("tnt-tag.yml", TntTagConfiguration.class);
        assertNotNull(config);
        assertTrue(config.fuseSeconds() > 0);
    }

    @Test
    void flowerCrownConfigParses() throws Exception {
        FlowerCrownConfiguration config = ConfigTestSupport.load("flower-crown.yml", FlowerCrownConfiguration.class);
        assertNotNull(config);
        assertTrue(config.flowersToWin() > 0);
        assertNotNull(config.flowerMaterials());
    }
}

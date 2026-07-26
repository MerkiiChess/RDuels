package ru.merkii.rduels.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.ResourceConfiguration;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.messages.PluginMessageConfig;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.arena.config.ArenaConfiguration;
import ru.merkii.rduels.core.customkit.config.CustomKitConfiguration;
import ru.merkii.rduels.core.duel.config.DuelConfiguration;
import ru.merkii.rduels.core.elo.config.EloConfiguration;
import ru.merkii.rduels.core.party.config.PartyConfiguration;
import ru.merkii.rduels.core.bedwars.config.BedwarsConfiguration;
import ru.merkii.rduels.core.bedwars.config.BedwarsShopConfiguration;
import ru.merkii.rduels.core.bedwars.config.BedwarsUpgradeConfiguration;
import ru.merkii.rduels.core.boundary.config.BoundaryConfiguration;
import ru.merkii.rduels.core.economy.config.EconomyConfiguration;
import ru.merkii.rduels.core.flowercrown.config.FlowerCrownConfiguration;
import ru.merkii.rduels.core.goldenhead.config.GoldenHeadConfiguration;
import ru.merkii.rduels.core.killeffect.config.KillEffectConfiguration;
import ru.merkii.rduels.core.killmessage.config.KillMessageConfiguration;
import ru.merkii.rduels.core.killstreak.config.KillStreakConfiguration;
import ru.merkii.rduels.core.queue.config.QueueConfiguration;
import ru.merkii.rduels.core.randomkit.config.RandomKitConfiguration;
import ru.merkii.rduels.core.scoreboard.config.ScoreboardConfiguration;
import ru.merkii.rduels.core.skywars.config.SkywarsConfiguration;
import ru.merkii.rduels.core.sumo.config.SumoConfiguration;
import ru.merkii.rduels.core.tnttag.config.TntTagConfiguration;
import ru.merkii.rduels.core.sign.storage.SignStorage;

import java.io.IOException;

@Factory
public class ConfigFactory {

    @Bean
    public RDuels plugin() {
        return RDuels.getInstance();
    }

    @Bean
    public ResourceConfiguration resourceConfiguration(RDuels plugin) {
        try {
            return new ResourceConfiguration(plugin);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Bean
    public MenuConfiguration menuConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("menu.yml", MenuConfiguration.class);
    }

    @Bean
    public DuelConfiguration duelConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("duel.yml", DuelConfiguration.class);
    }

    @Bean
    public CustomKitConfiguration customKitConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("custom-kits.yml", CustomKitConfiguration.class);
    }

    @Bean
    public MessageConfig messageConfig(ResourceConfiguration resourceConfiguration) {
        PluginMessageConfig messageConfig = resourceConfiguration.getConfig("messages.yml", PluginMessageConfig.class);
        return messageConfig.messages();
    }

    @Bean
    public ArenaConfiguration arenaConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("arenas.yml", ArenaConfiguration.class);
    }

    @Bean
    public PartyConfiguration partyConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("party.yml", PartyConfiguration.class);
    }

    @Bean
    public KitConfiguration kitConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("kits.yml", KitConfiguration.class);
    }

    @Bean
    public SettingsConfiguration settingsConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("settings.yml", SettingsConfiguration.class);
    }

    @Bean
    public EloConfiguration eloConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("elo.yml", EloConfiguration.class);
    }

    @Bean
    public ScoreboardConfiguration scoreboardConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("scoreboard.yml", ScoreboardConfiguration.class);
    }

    @Bean
    public QueueConfiguration queueConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("queue.yml", QueueConfiguration.class);
    }

    @Bean
    public KillEffectConfiguration killEffectConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("kill-effects.yml", KillEffectConfiguration.class);
    }

    @Bean
    public KillStreakConfiguration killStreakConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("kill-streaks.yml", KillStreakConfiguration.class);
    }

    @Bean
    public KillMessageConfiguration killMessageConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("kill-messages.yml", KillMessageConfiguration.class);
    }

    @Bean
    public GoldenHeadConfiguration goldenHeadConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("golden-head.yml", GoldenHeadConfiguration.class);
    }

    @Bean
    public BoundaryConfiguration boundaryConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("boundary.yml", BoundaryConfiguration.class);
    }

    @Bean
    public SumoConfiguration sumoConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("sumo.yml", SumoConfiguration.class);
    }

    @Bean
    public EconomyConfiguration economyConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("economy.yml", EconomyConfiguration.class);
    }

    @Bean
    public RandomKitConfiguration randomKitConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("random-kits.yml", RandomKitConfiguration.class);
    }

    @Bean
    public SkywarsConfiguration skywarsConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("skywars-loot.yml", SkywarsConfiguration.class);
    }

    @Bean
    public BedwarsConfiguration bedwarsConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("bedwars.yml", BedwarsConfiguration.class);
    }

    @Bean
    public BedwarsShopConfiguration bedwarsShopConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("bedwars-shop.yml", BedwarsShopConfiguration.class);
    }

    @Bean
    public BedwarsUpgradeConfiguration bedwarsUpgradeConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("bedwars-upgrades.yml", BedwarsUpgradeConfiguration.class);
    }

    @Bean
    public TntTagConfiguration tntTagConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("tnt-tag.yml", TntTagConfiguration.class);
    }

    @Bean
    public FlowerCrownConfiguration flowerCrownConfiguration(ResourceConfiguration resourceConfiguration) {
        return resourceConfiguration.getConfig("flower-crown.yml", FlowerCrownConfiguration.class);
    }

    @Bean
    public SignStorage signStorage(RDuels plugin) {
        return plugin.loadSettings("signStorage.json", SignStorage.class);
    }

}

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
import ru.merkii.rduels.core.party.config.PartyConfiguration;
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
    public SignStorage signStorage(RDuels plugin) {
        return plugin.loadSettings("signStorage.json", SignStorage.class);
    }

}

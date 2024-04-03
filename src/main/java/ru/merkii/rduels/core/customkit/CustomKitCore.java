package ru.merkii.rduels.core.customkit;

import lombok.Getter;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.Config;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.customkit.api.provider.CustomKitAPIProvider;
import ru.merkii.rduels.core.customkit.command.CustomKitCommand;
import ru.merkii.rduels.core.customkit.config.CustomKitConfig;
import ru.merkii.rduels.core.customkit.listener.CustomKitListener;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;

@Getter
public class CustomKitCore implements Core {

    public static CustomKitCore INSTANCE;
    private CustomKitConfig customKitConfig;
    private CustomKitStorage customKitStorage;
    private CustomKitAPI customKitAPI;

    @Override
    public void enable(RDuels plugin) {
        INSTANCE = this;
        plugin.registerListeners(new CustomKitListener());
        reloadConfig(plugin);
        customKitStorage = new CustomKitStorage();
        customKitAPI = new CustomKitAPIProvider();
        plugin.registerCommands(new CustomKitCommand());
    }

    @Override
    public void disable(RDuels plugin) {
        INSTANCE = null;
    }

    @Override
    public void reloadConfig(RDuels plugin) {
        this.customKitConfig = Config.load(plugin, "customkitconfig.json", CustomKitConfig.class);
    }
}

package ru.merkii.rduels;

import io.avaje.inject.BeanScope;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.merkii.rduels.config.settings.Config;
import ru.merkii.rduels.core.Core;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class RDuels extends JavaPlugin {

    private static BeanScope beanScope;
    private final List<Core> cores = new ArrayList<>();

    @Override
    public void onEnable() {
        beanScope = BeanScope.builder()
                .bean(RDuels.class, this)
                .classLoader(getClassLoader())
                .build();
        beanScope.get(PluginBootstrap.class).initialize(this);
    }

    @Override
    public void onDisable() {
        if (!cores.isEmpty()) {
            cores.forEach(core -> core.disable(this));
        }
        if (beanScope != null) {
            beanScope.close();
        }
    }

    public static BeanScope beanScope() {
        return beanScope;
    }

    public <T extends Config> T loadSettings(String file, Class<T> clazz) {
        return Config.load(this, file, clazz);
    }

    public void reloadConfigs() {
        for (Core core : this.cores) {
            core.reloadConfig(this);
        }
        this.saveConfig();
    }

    public void debug(String str) {
    }

    @SafeVarargs
    public final <T extends Listener> void registerListeners(Class<T>... clazzs) {
        PluginManager pluginManager = this.getServer().getPluginManager();
        for (Class<T> clazz : clazzs) {
            Listener listener = beanScope.get(clazz);
            pluginManager.registerEvents(listener, this);
        }
    }

    public static RDuels getInstance() {
        return beanScope().get(RDuels.class);
    }

}

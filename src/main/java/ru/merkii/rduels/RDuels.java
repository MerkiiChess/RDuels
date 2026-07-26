package ru.merkii.rduels;

import io.avaje.inject.BeanScope;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.merkii.rduels.config.settings.Config;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.statistic.StatisticService;

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
            // Persist any cached statistics synchronously while the database is still open.
            beanScope.get(StatisticService.class).flushAllBlocking();
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

    public static RDuels getInstance() {
        return beanScope().get(RDuels.class);
    }

}

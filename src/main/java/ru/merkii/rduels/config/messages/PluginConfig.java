package ru.merkii.rduels.config.messages;

import com.bivashy.configuration.BukkitConfigurationProcessor;
import com.bivashy.configuration.ConfigurationProcessor;
import com.bivashy.configuration.annotation.ConfigField;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
public class PluginConfig {

    public static final ConfigurationProcessor CONFIGURATION_PROCESSOR = new BukkitConfigurationProcessor();

    @ConfigField("messages")
    private MessageConfiguration messages;

    public PluginConfig(Plugin plugin) {
        plugin.saveDefaultConfig();
        CONFIGURATION_PROCESSOR.resolve(plugin.getConfig(), this);
    }

}

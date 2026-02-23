package ru.merkii.rduels.config;

import com.bivashy.configurate.objectmapping.common.InterfaceObjectMapperFactory;
import jakarta.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.messages.PluginMessageConfig;
import ru.merkii.rduels.config.serializer.PluginConfigBasicSerializers;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.arena.config.ArenaConfiguration;
import ru.merkii.rduels.core.customkit.config.CustomKitConfiguration;
import ru.merkii.rduels.core.duel.config.DuelConfiguration;
import ru.merkii.rduels.core.party.config.PartyConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ResourceConfiguration {

    private final Plugin plugin;
    private final Map<String, ConfigurationNode> nodes = new HashMap<>();
    private final Map<String, Object> configs = new HashMap<>();

    public ResourceConfiguration(Plugin plugin) throws IOException {
        this.plugin = plugin;
        loadConfiguration("kits.yml", KitConfiguration.class);
        loadConfiguration("menu.yml", MenuConfiguration.class);
        loadConfiguration("settings.yml", SettingsConfiguration.class);
        loadConfiguration("messages.yml", PluginMessageConfig.class);
        loadConfiguration("arenas.yml", ArenaConfiguration.class);
        loadConfiguration("custom-kits.yml", CustomKitConfiguration.class);
        loadConfiguration("duel.yml", DuelConfiguration.class);
        loadConfiguration("party.yml", PartyConfiguration.class);
    }

    private <T> void loadConfiguration(String fileName, Class<T> configClass) throws IOException {
        Path dataFolder = plugin.getDataFolder().toPath();
        Path configPath = dataFolder.resolve(fileName);

        if (!Files.exists(configPath)) {
            Files.createDirectories(dataFolder);
            try (InputStream resourceStream = plugin.getClass().getResourceAsStream("/" + fileName)) {
                if (resourceStream == null) {
                    throw new NullPointerException("Cannot find resource: " + fileName);
                }
                Files.copy(resourceStream, configPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        ObjectMapper.Factory objectMapperFactory = new InterfaceObjectMapperFactory();
        ConfigurationNode node = YamlConfigurationLoader.builder()
                .path(configPath)
                .defaultOptions(opt -> opt.serializers(builder ->
                        builder.register(InterfaceObjectMapperFactory::applicable, objectMapperFactory.asTypeSerializer())
                                .registerAll(PluginConfigBasicSerializers.serializers())
                                .registerAll(TypeSerializerCollection.defaults())))
                .build()
                .load();

        T config = node.get(configClass);

        nodes.put(fileName, node);
        configs.put(fileName, config);
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String fileName, Class<T> clazz) {
        Object config = configs.get(fileName);
        if (config == null) {
            try {
                loadConfiguration(fileName, clazz);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return (T) config;
    }

    public <T> void updateAndSave(String fileName, Class<T> configClass, T configInstance) throws IOException {
        ConfigurationNode node = getNode(fileName);
        if (node == null) {
            throw new IllegalArgumentException("Нет узла для " + fileName);
        }
        node.set(configClass, configInstance);
        saveConfiguration(fileName);
    }

    public ConfigurationNode getNode(String fileName) {
        return nodes.get(fileName);
    }

    public void saveConfiguration(String fileName) throws IOException {
        ConfigurationNode node = getNode(fileName);
        if (node == null) {
            return;
        }
        Path configPath = plugin.getDataFolder().toPath().resolve(fileName);
        YamlConfigurationLoader.builder()
                .path(configPath)
                .build()
                .save(node);
    }

    public void reloadAll() throws IOException {
        nodes.clear();
        configs.clear();
        loadConfiguration("kits.yml", KitConfiguration.class);
        loadConfiguration("settings.yml", SettingsConfiguration.class);
        loadConfiguration("messages.yml", MessageConfig.class);
        loadConfiguration("arenas.yml", ArenaConfiguration.class);
        loadConfiguration("custom-kits.yml", CustomKitConfiguration.class);
        loadConfiguration("duel.yml", DuelConfiguration.class);
        loadConfiguration("party.yml", PartyConfiguration.class);
    }

}
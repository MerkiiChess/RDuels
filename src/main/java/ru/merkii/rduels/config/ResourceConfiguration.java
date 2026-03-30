package ru.merkii.rduels.config;

import com.bivashy.configurate.objectmapping.common.InterfaceObjectMapperFactory;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import ru.merkii.rduels.config.category.CategoryItemConfiguration;
import ru.merkii.rduels.config.enchant.EnchantItemConfiguration;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResourceConfiguration {

    Plugin plugin;
    Map<String, ConfigurationNode> nodes = new HashMap<>();
    Map<String, Object> configs = new HashMap<>();
    Map<String, Class<?>> configTypes = new LinkedHashMap<>();

    public ResourceConfiguration(Plugin plugin) throws IOException {
        this.plugin = plugin;
        registerConfiguration("kits.yml", KitConfiguration.class);
        registerConfiguration("menu.yml", MenuConfiguration.class);
        registerConfiguration("settings.yml", SettingsConfiguration.class);
        registerConfiguration("messages.yml", PluginMessageConfig.class);
        registerConfiguration("arenas.yml", ArenaConfiguration.class);
        registerConfiguration("custom-kits.yml", CustomKitConfiguration.class);
        registerConfiguration("duel.yml", DuelConfiguration.class);
        registerConfiguration("party.yml", PartyConfiguration.class);
        registerConfiguration("category-items.yml", CategoryItemConfiguration.class);
        registerConfiguration("enchants-items.yml", EnchantItemConfiguration.class);
        loadAll();
    }

    private void registerConfiguration(String fileName, Class<?> configClass) {
        configTypes.put(fileName, configClass);
    }

    private void loadAll() throws IOException {
        for (Map.Entry<String, Class<?>> entry : configTypes.entrySet()) {
            loadConfigurationUnchecked(entry.getKey(), entry.getValue(), false);
        }
    }

    private <T> void loadConfiguration(String fileName, Class<T> configClass) throws IOException {
        loadConfiguration(fileName, configClass, false);
    }

    private <T> void loadConfiguration(String fileName, Class<T> configClass, boolean reloadExisting) throws IOException {
        Path dataFolder = plugin.getDataFolder().toPath();
        Path configPath = dataFolder.resolve(fileName);

        if (!Files.exists(configPath)) {
            Files.createDirectories(dataFolder);
            try (InputStream resourceStream = plugin.getClass().getResourceAsStream("/" + fileName)) {
                if (resourceStream == null) {
                    throw new IOException("Не найден встроенный ресурс " + fileName);
                }
                Files.copy(resourceStream, configPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        ConfigurationNode loadedNode = createLoader(configPath).load();
        T config = Objects.requireNonNull(loadedNode.get(configClass), "Config was not loaded: " + fileName);

        if (reloadExisting && nodes.containsKey(fileName)) {
            ConfigurationNode currentNode = nodes.get(fileName);
            currentNode.raw(loadedNode.raw());
            configs.put(fileName, Objects.requireNonNull(currentNode.get(configClass), "Config was not reloaded: " + fileName));
            return;
        }

        nodes.put(fileName, loadedNode);
        configs.put(fileName, config);
    }

    @SuppressWarnings("unchecked")
    private void loadConfigurationUnchecked(String fileName, Class<?> configClass, boolean reloadExisting) throws IOException {
        loadConfiguration(fileName, (Class<Object>) configClass, reloadExisting);
    }

    private YamlConfigurationLoader createLoader(Path configPath) {
        ObjectMapper.Factory objectMapperFactory = new InterfaceObjectMapperFactory();
        return YamlConfigurationLoader.builder()
                .path(configPath)
                .defaultOptions(opt -> opt.serializers(builder ->
                        builder.register(InterfaceObjectMapperFactory::applicable, objectMapperFactory.asTypeSerializer())
                                .registerAll(PluginConfigBasicSerializers.serializers())
                                .registerAll(TypeSerializerCollection.defaults())))
                .build();
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String fileName, Class<T> clazz) {
        Object config = configs.get(fileName);
        if (config == null) {
            try {
                loadConfiguration(fileName, clazz);
                config = configs.get(fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return clazz.cast(config);
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
        createLoader(configPath).save(node);
    }

    public void reloadAll() throws IOException {
        for (Map.Entry<String, Class<?>> entry : configTypes.entrySet()) {
            loadConfigurationUnchecked(entry.getKey(), entry.getValue(), true);
        }
    }

}

package ru.merkii.rduels.config;

import com.bivashy.configurate.objectmapping.common.InterfaceObjectMapperFactory;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import ru.merkii.rduels.config.serializer.PluginConfigBasicSerializers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Loads a bundled config resource through exactly the same Configurate pipeline the
 * plugin uses at runtime (see {@code ResourceConfiguration}), but from the classpath —
 * so tests can verify the shipped .yml files parse into their config interfaces without
 * booting a server.
 */
public final class ConfigTestSupport {

    private ConfigTestSupport() {
    }

    public static <T> T load(String resourceName, Class<T> type) throws Exception {
        ObjectMapper.Factory objectMapperFactory = new InterfaceObjectMapperFactory();
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .source(() -> reader(resourceName))
                .defaultOptions(opt -> opt.serializers(builder ->
                        builder.register(InterfaceObjectMapperFactory::applicable, objectMapperFactory.asTypeSerializer())
                                .registerAll(PluginConfigBasicSerializers.serializers())
                                .registerAll(TypeSerializerCollection.defaults())))
                .build();
        ConfigurationNode node = loader.load();
        return node.get(type);
    }

    private static BufferedReader reader(String resourceName) {
        InputStream stream = ConfigTestSupport.class.getResourceAsStream("/" + resourceName);
        Objects.requireNonNull(stream, "Ресурс не найден на classpath: " + resourceName);
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }
}

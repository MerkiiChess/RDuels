package ru.merkii.rduels.config.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.config.menu.messages.MessagesMenuConfiguration;
import ru.merkii.rduels.config.Placeholder;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class MessagesConfigurationSerializer implements TypeSerializer<MessagesMenuConfiguration> {

    private final ComponentSerializer<Component, Component, String> componentSerializer;

    public MessagesConfigurationSerializer() {
        this.componentSerializer = null;
    }

    @Override
    public MessagesMenuConfiguration deserialize(Type type, ConfigurationNode value) throws SerializationException {
        try {
            return new MessagesConfigurationImpl(value, componentSerializer);
        } catch (SerializationException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(Type type, MessagesMenuConfiguration obj, ConfigurationNode value) {
        throw new UnsupportedOperationException("Serialization of 'MessagesConfiguration' doesn't supported");
    }

    private static class MessagesConfigurationImpl implements MessagesMenuConfiguration {

        private static final TextComponent EMPTY_TEXT = Component.text("Error occurred during message loading");
        private final Map<String, Component> messages = new HashMap<>();
        private final Map<String, MessagesMenuConfiguration> subs = new HashMap<>();

        public MessagesConfigurationImpl(ConfigurationNode node,
                                         ComponentSerializer<? extends Component, ? extends Component, String> componentSerializer) throws SerializationException {
            boolean serializerProvided = componentSerializer != null;
            if (!serializerProvided) {
                ComponentSerializerProviders serializerProvider = requireNonNull(node.node("serializer").get(ComponentSerializerProviders.class), "serializer");
                componentSerializer = serializerProvider.componentSerializer();
            }

            for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
                String key = entry.getKey().toString();
                if (!serializerProvided && key.equals("serializer"))
                    continue;
                ConfigurationNode value = entry.getValue();
                if (value.isMap()) {
                    subs.put(key, new MessagesConfigurationImpl(value, componentSerializer));
                } else {
                    messages.put(key, componentSerializer.deserialize(value.getString(EMPTY_TEXT.content())));
                }
            }
        }

        @Override
        public Component message(Placeholder.Placeholders placeholders, String key, String... keys) {
            if (keys.length == 0)
                return applyPlaceholders(messages.get(key), placeholders);
            MessagesMenuConfiguration sub = sub(key);
            if (sub == null)
                return EMPTY_TEXT;
            if (keys.length == 1)
                return applyPlaceholders(sub.message(keys[0]), placeholders);
            return applyPlaceholders(sub.message(keys[0], Arrays.copyOfRange(keys, 1, keys.length)), placeholders);
        }

        private Component applyPlaceholders(Component component, Placeholder.Placeholders placeholders) {
            for (Placeholder placeholder : placeholders.placeholders())
                component = component.replaceText(builder -> builder.matchLiteral(placeholder.match()).replacement(placeholder.replacement()));
            return component;
        }

        @Override
        public MessagesMenuConfiguration sub(String key) {
            return subs.get(key);
        }

    }

}

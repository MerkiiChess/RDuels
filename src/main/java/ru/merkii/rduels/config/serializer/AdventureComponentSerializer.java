package ru.merkii.rduels.config.serializer;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;

public class AdventureComponentSerializer implements TypeSerializer<Component> {

    private static ComponentSerializerProviders serializerProvider = ComponentSerializerProviders.MINI_MESSAGE;

    @Override
    public Component deserialize(Type type, ConfigurationNode node) {
        return serializerProvider.componentSerializer().deserialize(node.getString("<red>[ERROR] Node '" + node.key() + "' doesn't present string."));
    }

    @Override
    public void serialize(Type type, @Nullable Component obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(String.class, "");
            return;
        }
        node.set(String.class, serializerProvider.componentSerializer().serialize(obj));
    }

    public static void setSerializerProvider(ComponentSerializerProviders serializerProvider) {
        AdventureComponentSerializer.serializerProvider = requireNonNull(serializerProvider);
    }

}

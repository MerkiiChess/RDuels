package ru.merkii.rduels.config.serializer;

import org.bukkit.Sound;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class SoundSerializer implements TypeSerializer<Sound> {
    @Override
    public Sound deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String sound = node.getString();
        return Sound.valueOf(sound);
    }

    @Override
    public void serialize(Type type, @Nullable Sound obj, ConfigurationNode node) throws SerializationException {
        node.set(obj.name());
    }
}

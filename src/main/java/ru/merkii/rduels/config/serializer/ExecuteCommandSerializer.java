package ru.merkii.rduels.config.serializer;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.config.model.ExecuteCommand;

import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;

public class ExecuteCommandSerializer implements TypeSerializer<ExecuteCommand> {

    @Override
    public ExecuteCommand deserialize(Type type, ConfigurationNode node) {
        String rawCommand = requireNonNull(node.getString());
        return new ExecuteCommand(rawCommand);
    }

    @Override
    public void serialize(Type type, @Nullable ExecuteCommand obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set("");
            return;
        }
        node.set(obj.prefix() + obj.command());
    }

}

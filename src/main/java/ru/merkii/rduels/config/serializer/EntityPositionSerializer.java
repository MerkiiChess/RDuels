package ru.merkii.rduels.config.serializer;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.model.EntityPosition;

import java.lang.reflect.Type;

public class EntityPositionSerializer implements TypeSerializer<EntityPosition> {

    @Override
    public EntityPosition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String world = node.node("world").getString();
        double x = node.node("x").getDouble();
        double y = node.node("y").getDouble();
        double z = node.node("z").getDouble();
        float pitch = node.node("pitch").getFloat(0.0f);
        float yaw = node.node("yaw").getFloat(0.0f);

        return new EntityPosition(world, x, y, z, pitch, yaw);
    }

    @Override
    public void serialize(Type type, EntityPosition value, ConfigurationNode node) throws SerializationException {
        if (value == null) {
            return;
        }
        node.node("world").set(value.getWorldName());
        node.node("x").set(value.getX());
        node.node("y").set(value.getY());
        node.node("z").set(value.getZ());
        node.node("pitch").set(value.getPitch());
        node.node("yaw").set(value.getYaw());
    }

}
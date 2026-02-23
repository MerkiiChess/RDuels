package ru.merkii.rduels.config.serializer;

import org.bukkit.Material;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class MaterialSerializer implements TypeSerializer<Material> {

    @Override
    public Material deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String materialName = node.getString();
        Material material = Material.getMaterial(materialName);
        return material != null ? material : Material.AIR;
    }

    @Override
    public void serialize(Type type, Material value, ConfigurationNode node) throws SerializationException {
        node.set(value != null ? value.name() : "AIR");
    }

}
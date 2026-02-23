package ru.merkii.rduels.config.serializer;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;

import java.lang.reflect.Type;
import java.util.List;

public class CustomKitEnchantCategorySerializer implements TypeSerializer<CustomKitEnchantCategory> {

    @Override
    public CustomKitEnchantCategory deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String nameEnchant = node.node("name-enchant").getString();
        int lvl = node.node("lvl").getInt();
        int slot = node.node("slot").getInt();
        List<String> materials = node.node("materials").getList(String.class);
        return new CustomKitEnchantCategory(nameEnchant, lvl, slot, materials);
    }

    @Override
    public void serialize(Type type, CustomKitEnchantCategory value, ConfigurationNode node) throws SerializationException {
        // Implement if saving is required
    }

}
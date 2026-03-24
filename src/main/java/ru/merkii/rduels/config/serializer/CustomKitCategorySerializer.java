package ru.merkii.rduels.config.serializer;

import org.bukkit.Material;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.builder.ItemBuilder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomKitCategorySerializer implements TypeSerializer<CustomKitCategory> {

    @Override
    public CustomKitCategory deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Material material = Material.valueOf(node.node("display-material").getString());
        String id = node.node("id").getString();
        String displayName = node.node("display-name").getString();
        List<Material> items = node.node("items").getList(Material.class);
        return new CustomKitCategory(id, displayName, material, items);
    }

    @Override
    public void serialize(Type type, CustomKitCategory value, ConfigurationNode node) throws SerializationException {
        node.node("display-material").set(value.getDisplayMaterial());
        node.node("display-name").set(value.getDisplayName());
        node.node("id").set(value.getId());
        if (!value.getItems().isEmpty())
            node.node("items").setList(Material.class, value.getItems());
    }

}
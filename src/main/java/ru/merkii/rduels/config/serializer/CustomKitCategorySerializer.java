package ru.merkii.rduels.config.serializer;

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
        InventoryItem item = node.get(InventoryItem.class);
        String id = node.node("id").getString();
        List<InventoryItem> items = node.node("items").getList(InventoryItem.class);
        return new CustomKitCategory(id, item, items);
    }

    @Override
    public void serialize(Type type, CustomKitCategory value, ConfigurationNode node) throws SerializationException {
        // Implement if saving is required
    }

}
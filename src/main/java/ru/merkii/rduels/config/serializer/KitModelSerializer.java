package ru.merkii.rduels.config.serializer;

import org.bukkit.Material;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.model.KitModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitModelSerializer implements TypeSerializer<KitModel> {

    @Override
    public KitModel deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String displayName = node.node("display-name").getString();
        int slot = node.node("slot").getInt();
        List<String> lore = node.node("lore").getList(String.class, new ArrayList<>());
        String displayMaterialStr = node.node("display-material").getString("DIAMOND_CHESTPLATE");
        Material displayMaterial = Material.getMaterial(displayMaterialStr);
        if (displayMaterial == null) {
            displayMaterial = Material.DIAMOND_CHESTPLATE;
        }

        ConfigurationNode itemsNode = node.node("items");
        Map<Integer, ItemBuilder> items = new HashMap<>();
        if (!itemsNode.empty()) {
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : itemsNode.childrenMap().entrySet()) {
                Integer key = Integer.parseInt(entry.getKey().toString());
                ItemBuilder itemBuilder = entry.getValue().get(ItemBuilder.class);
                items.put(key, itemBuilder);
            }
        }

        boolean bindingArena = node.node("binding-arena").getBoolean(false);
        List<String> arenasName = node.node("arenas-name").getList(String.class, new ArrayList<>());

        return new KitModel(displayName, slot, lore, displayMaterial, items, bindingArena, arenasName);
    }

    @Override
    public void serialize(Type type, KitModel value, ConfigurationNode node) throws SerializationException {
        if (value == null) {
            node.raw(null);
            return;
        }

        node.node("display-name").set(value.getDisplayName());
        node.node("slot").set(value.getSlot());
        node.node("lore").setList(String.class, value.getLore());
        node.node("display-material").set(value.getDisplayMaterial().name());

        ConfigurationNode itemsNode = node.node("items");
        itemsNode.raw(null);

        if (value.getItems() != null && !value.getItems().isEmpty()) {
            for (Map.Entry<Integer, ItemBuilder> entry : value.getItems().entrySet()) {
                itemsNode.node(String.valueOf(entry.getKey()))
                        .set(entry.getValue());
            }
        }

        node.node("binding-arena").set(value.isBindingArena());
        node.node("arenas-name").setList(String.class, value.getArenasName());
    }

}
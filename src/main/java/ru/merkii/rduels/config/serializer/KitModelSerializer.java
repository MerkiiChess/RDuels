package ru.merkii.rduels.config.serializer;

import org.bukkit.Color;
import org.bukkit.Material;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.model.KitModel;
import ru.merkii.rduels.model.KitRules;

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

        return new KitModel(displayName, slot, lore, displayMaterial, items, bindingArena, arenasName, deserializeRules(node.node("rules")));
    }

    private KitRules deserializeRules(ConfigurationNode rulesNode) {
        if (rulesNode.empty()) {
            return KitRules.DEFAULT;
        }
        double maxHealth = rulesNode.node("max-hearts").getDouble(10.0D) * 2.0D;
        double damageMultiplier = rulesNode.node("damage-multiplier").getDouble(1.0D);
        boolean noHunger = rulesNode.node("no-hunger").getBoolean(false);
        boolean disableCrafting = rulesNode.node("disable-crafting").getBoolean(false);
        int buildHeight = rulesNode.node("build-height").getInt(-1);
        int blockDecaySeconds = rulesNode.node("block-decay-seconds").getInt(0);

        Map<Material, Integer> itemCooldowns = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : rulesNode.node("item-cooldowns").childrenMap().entrySet()) {
            Material material = Material.getMaterial(entry.getKey().toString().toUpperCase());
            if (material != null) {
                itemCooldowns.put(material, entry.getValue().getInt(0));
            }
        }

        Color armorColor = parseColor(rulesNode.node("armor-color").getString());

        return new KitRules(maxHealth, damageMultiplier, noHunger, disableCrafting, buildHeight, itemCooldowns, blockDecaySeconds, armorColor);
    }

    /** Parses an "r,g,b" armour colour string; returns null when absent or malformed. */
    private Color parseColor(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String[] parts = raw.split(",");
        if (parts.length != 3) {
            return null;
        }
        try {
            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());
            return Color.fromRGB(clamp(r), clamp(g), clamp(b));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
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

        KitRules rules = value.getRules();
        if (rules != null && rules != KitRules.DEFAULT) {
            ConfigurationNode rulesNode = node.node("rules");
            rulesNode.node("max-hearts").set(rules.getMaxHealth() / 2.0D);
            rulesNode.node("damage-multiplier").set(rules.getDamageMultiplier());
            rulesNode.node("no-hunger").set(rules.isNoHunger());
            rulesNode.node("disable-crafting").set(rules.isDisableCrafting());
            rulesNode.node("build-height").set(rules.getBuildHeight());
            rulesNode.node("block-decay-seconds").set(rules.getBlockDecaySeconds());
            for (Map.Entry<Material, Integer> entry : rules.getItemCooldowns().entrySet()) {
                rulesNode.node("item-cooldowns", entry.getKey().name()).set(entry.getValue());
            }
            if (rules.hasArmorColor()) {
                Color color = rules.getArmorColor();
                rulesNode.node("armor-color").set(color.getRed() + "," + color.getGreen() + "," + color.getBlue());
            }
        }
    }

}
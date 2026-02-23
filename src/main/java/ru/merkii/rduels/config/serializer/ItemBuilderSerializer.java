package ru.merkii.rduels.config.serializer;

import org.bukkit.inventory.ItemFlag;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.builder.ItemBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilderSerializer implements TypeSerializer<ItemBuilder> {

    @Override
    public ItemBuilder deserialize(Type type, ConfigurationNode node) throws SerializationException {
        ItemBuilder builder = ItemBuilder.builder();
        String materialStr = node.node("material").getString();
        if (materialStr != null) {
            builder.setMaterial(materialStr);
        }
        builder.setAmount(node.node("amount").getInt(1));
        builder.setDisplayName(node.node("display-name").getString());
        builder.setLore(node.node("lore").getList(String.class));
        builder.setSlot(node.node("slot").getInt());
        List<String> flagStrings = node.node("item-flags").getList(String.class);
        if (flagStrings != null && !flagStrings.isEmpty()) {
            ItemFlag[] flags = flagStrings.stream()
                    .map(ItemFlag::valueOf)
                    .toArray(ItemFlag[]::new);
            builder.addItemFlags(flags);
        }
        ConfigurationNode enchantsNode = node.node("enchants");
        if (!enchantsNode.empty()) {
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : enchantsNode.childrenMap().entrySet()) {
                String key = entry.getKey().toString().toLowerCase();
                int lvl = entry.getValue().getInt(1);
                builder.addEnchant(key, lvl);
            }
        }
        String basePotionEffect = node.node("base-potion-effect").getString();
        if (basePotionEffect != null) {
            builder.setPotionEffect(basePotionEffect);
        }
        List<String> potionEffects = node.node("potion-effects").getList(String.class);
        if (potionEffects != null) {
            builder.setPotionEffects(potionEffects);
        }
        return builder;
    }

    @Override
    public void serialize(Type type, ItemBuilder value, ConfigurationNode node) throws SerializationException {
        if (value == null) return;
        if (value.getMaterial() != null)
            node.node("material").set(value.getMaterial().toString());

        node.node("amount").set(value.getAmount() <= 0 ? 1 : value.getAmount());

        if (value.getDisplayName() != null)
            node.node("display-name").set(value.getDisplayName());

        if (value.getLore() != null && !value.getLore().isEmpty())
            node.node("lore").setList(String.class, value.getLore());

        if (value.getItemFlags() != null && value.getItemFlags().length != 0)
            node.node("item-flags").setList(String.class, Arrays.stream(value.getItemFlags()).map(ItemFlag::toString).toList());

        if (value.getEnchants() != null && !value.getEnchants().isEmpty()) {
            List<String> enchants = new ArrayList<>();
            value.getEnchants().forEach((enchant, lvl) -> enchants.add(enchant + ":" + lvl));
            node.node("enchants").setList(String.class, enchants);
        }
        if (value.getBasePotionEffect() != null)
            node.node("base-potion-effect").set(value.getBasePotionEffect());
        if (value.getPotionEffect() != null && !value.getPotionEffect().isEmpty())
            node.node("potion-effects").setList(String.class, value.getPotionEffect());
    }

}
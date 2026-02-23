package ru.merkii.rduels.config.serializer;

import org.bukkit.Material;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.model.EntityPosition;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaModelSerializer implements TypeSerializer<ArenaModel> {

    @Override
    public ArenaModel deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String arenaName = node.node("arena-name").getString();
        String displayName = node.node("display-name").getString();
        String materialStr = node.node("material").getString("STONE");
        Material material = Material.getMaterial(materialStr);
        if (material == null) {
            material = Material.STONE;
        }
        EntityPosition onePosition = node.node("one-position").get(EntityPosition.class);
        EntityPosition twoPosition = node.node("two-position").get(EntityPosition.class);
        EntityPosition spectatorPosition = node.node("spectator-position").get(EntityPosition.class);

        ConfigurationNode ffaPositionsNode = node.node("ffa-positions");
        Map<Integer, EntityPosition> ffaPositions = new HashMap<>();
        if (!ffaPositionsNode.empty()) {
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : ffaPositionsNode.childrenMap().entrySet()) {
                Integer key = Integer.parseInt(entry.getKey().toString());
                EntityPosition pos = entry.getValue().get(EntityPosition.class);
                ffaPositions.put(key, pos);
            }
        }

        EntityPosition schematicPosition = node.node("schematic-position").get(EntityPosition.class);
        boolean customKits = node.node("custom-kits").getBoolean(false);
        List<String> customKitsName = node.node("custom-kits-name").getList(String.class, new ArrayList<>());
        boolean ffa = node.node("ffa").getBoolean(false);
        boolean breaking = node.node("breaking").getBoolean(false);
        String schematic = node.node("schematic").getString("NO_SCHEMATIC");
        int radiusDeleteBlocks = node.node("radius-delete-blocks").getInt(50);

        return new ArenaModel(arenaName, displayName, material, onePosition, twoPosition, spectatorPosition, ffaPositions, schematicPosition, customKits, customKitsName, ffa, breaking, schematic, radiusDeleteBlocks);
    }

    @Override
    public void serialize(Type type, ArenaModel value, ConfigurationNode node) throws SerializationException {
        if (value == null) {
            return;
        }
        node.node("arena-name").set(value.getArenaName());
        node.node("display-name").set(value.getDisplayName());
        node.node("material").set(value.getMaterial());
        if (value.getOnePosition() != null)
            node.node("one-position").set(value.getOnePosition());

        if (value.getTwoPosition() != null)
            node.node("two-position").set(value.getTwoPosition());

        if (value.getSpectatorPosition() != null)
            node.node("spectator-position").set(value.getSpectatorPosition());
        if (value.getFfaPositions() != null && !value.getFfaPositions().isEmpty()) {
            ConfigurationNode ffaPositionsNode = node.node("ffa-positions");
            value.getFfaPositions().forEach((key, position) -> {
                try {
                    ffaPositionsNode.node(key).set(position);
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        if (value.getSpectatorPosition() != null)
            node.node("schematic-position").set(value.getSpectatorPosition());

        node.node("custom-kits").set(value.isCustomKits());
        if (value.getCustomKitsName() != null && !value.getCustomKitsName().isEmpty())
            node.node("custom-kits-name").setList(String.class, value.getCustomKitsName());

        node.node("ffa").set(value.isFfa());
        node.node("breaking").set(value.isBreaking());
        if (value.getSchematic() != null)
            node.node("schematic").set(value.getSchematic());

        int radiusDeleteBlock = value.getRadiusDeleteBlocks() <= 0 ? 50 : value.getRadiusDeleteBlocks();
        node.node("radius-delete-blocks").set(radiusDeleteBlock);
    }

}
package ru.merkii.rduels.core.arena.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import ru.merkii.rduels.model.EntityPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class ArenaModel implements Cloneable {

    final String arenaName;
    final String displayName;
    final Material material;
    @Setter
    EntityPosition onePosition;
    @Setter
    EntityPosition twoPosition;
    @Setter
    EntityPosition threePosition;
    @Setter
    EntityPosition fourPosition;
    @Setter
    EntityPosition spectatorPosition;
    @Setter
    Map<Integer, EntityPosition> ffaPositions;
    boolean customKits;
    List<String> customKitsName;
    final boolean ffa;
    final boolean breaking;
    final String schematic;

    public static ArenaModel create(String arenaName, String displayName, Material material, EntityPosition onePosition, EntityPosition twoPosition, EntityPosition threePosition, EntityPosition fourPosition, EntityPosition spectatorPosition, boolean ffa, boolean breaking, String schematic) {
        return new ArenaModel(arenaName, displayName, material, onePosition, twoPosition, threePosition, fourPosition, spectatorPosition, new HashMap<>(), false, new ArrayList<>(), ffa, breaking, schematic);
    }

    public static ArenaModel create(String arenaName, String displayName, Material material, EntityPosition onePosition, EntityPosition twoPosition, EntityPosition threePosition, EntityPosition fourPosition, EntityPosition spectatorPosition) {
        return new ArenaModel(arenaName, displayName, material, onePosition, twoPosition, threePosition, fourPosition, spectatorPosition, new HashMap<>(), false, new ArrayList<>(), false, false, "NO_SCHEMATIC");
    }

    @Override
    public ArenaModel clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (ArenaModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

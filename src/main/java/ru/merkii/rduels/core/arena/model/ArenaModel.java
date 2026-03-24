package ru.merkii.rduels.core.arena.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import ru.merkii.rduels.model.EntityPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArenaModel implements Cloneable {

    final String arenaName;
    final String displayName;
    final Material material;
    final boolean customKits;
    final List<String> customKitsName;
    final boolean ffa;
    final boolean breaking;
    final String schematic;
    final int radiusDeleteBlocks;
    @Setter
    EntityPosition onePosition;
    @Setter
    EntityPosition twoPosition;
    @Setter
    EntityPosition spectatorPosition;
    @Setter
    Map<Integer, EntityPosition> ffaPositions;
    @Setter
    EntityPosition schematicPosition;

    public static ArenaModel create(String arenaName, String displayName, Material material, EntityPosition onePosition, EntityPosition twoPosition, EntityPosition spectatorPosition, boolean ffa, boolean breaking, String schematic) {
        return new ArenaModel(
                arenaName,
                displayName,
                material,
                false,
                new ArrayList<>(),
                ffa,
                breaking,
                schematic,
                50,
                onePosition,
                twoPosition,
                spectatorPosition,
                new HashMap<>(),
                null);
    }

    public static ArenaModel create(String arenaName, String displayName, Material material, EntityPosition onePosition, EntityPosition twoPosition, EntityPosition spectatorPosition) {
        return new ArenaModel(
                arenaName,
                displayName,
                material,
                false,
                new ArrayList<>(),
                false,
                false,
                "NO_SCHEMATIC",
                50,
                onePosition,
                twoPosition,
                spectatorPosition,
                new HashMap<>(),
                null);
    }

    public ArenaModel clone() {
        try {
            return (ArenaModel)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static ArenaModelBuilder builder() {
        return new ArenaModelBuilder();
    }

    public static class ArenaModelBuilder {
        private String arenaName;
        private String displayName;
        private Material material;
        private EntityPosition onePosition;
        private EntityPosition twoPosition;
        private EntityPosition spectatorPosition;
        private Map<Integer, EntityPosition> ffaPositions;
        private EntityPosition schematicPosition;
        private boolean customKits;
        private List<String> customKitsName;
        private boolean ffa;
        private boolean breaking;
        private String schematic;
        private int radiusDeleteBlocks;

        ArenaModelBuilder() {
        }

        public ArenaModelBuilder arenaName(String arenaName) {
            this.arenaName = arenaName;
            return this;
        }

        public ArenaModelBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public ArenaModelBuilder material(Material material) {
            this.material = material;
            return this;
        }

        public ArenaModelBuilder onePosition(EntityPosition onePosition) {
            this.onePosition = onePosition;
            return this;
        }

        public ArenaModelBuilder twoPosition(EntityPosition twoPosition) {
            this.twoPosition = twoPosition;
            return this;
        }

        public ArenaModelBuilder spectatorPosition(EntityPosition spectatorPosition) {
            this.spectatorPosition = spectatorPosition;
            return this;
        }

        public ArenaModelBuilder ffaPositions(Map<Integer, EntityPosition> ffaPositions) {
            this.ffaPositions = ffaPositions;
            return this;
        }

        public ArenaModelBuilder schematicPosition(EntityPosition schematicPosition) {
            this.schematicPosition = schematicPosition;
            return this;
        }

        public ArenaModelBuilder customKits(boolean customKits) {
            this.customKits = customKits;
            return this;
        }

        public ArenaModelBuilder customKitsName(List<String> customKitsName) {
            this.customKitsName = customKitsName;
            return this;
        }

        public ArenaModelBuilder ffa(boolean ffa) {
            this.ffa = ffa;
            return this;
        }

        public ArenaModelBuilder breaking(boolean breaking) {
            this.breaking = breaking;
            return this;
        }

        public ArenaModelBuilder schematic(String schematic) {
            this.schematic = schematic;
            return this;
        }

        public ArenaModelBuilder radiusDeleteBlocks(int radiusDeleteBlocks) {
            this.radiusDeleteBlocks = radiusDeleteBlocks;
            return this;
        }

        public ArenaModel build() {
            return new ArenaModel(arenaName,
                    displayName,
                    material,
                    customKits,
                    customKitsName,
                    ffa,
                    breaking,
                    schematic,
                    radiusDeleteBlocks,
                    onePosition,
                    twoPosition,
                    spectatorPosition,
                    ffaPositions,
                    schematicPosition);
        }

        public String toString() {
            return "ArenaModel.ArenaModelBuilder(arenaName=" + this.arenaName + ", displayName=" + this.displayName + ", material=" + this.material + ", onePosition=" + this.onePosition + ", twoPosition=" + this.twoPosition + ", spectatorPosition=" + this.spectatorPosition + ", ffaPositions=" + this.ffaPositions + ", schematicPosition=" + this.schematicPosition + ", customKits=" + this.customKits + ", customKitsName=" + this.customKitsName + ", ffa=" + this.ffa + ", breaking=" + this.breaking + ", schematic=" + this.schematic + ", radiusDeleteBlocks=" + this.radiusDeleteBlocks + ")";
        }
    }
}

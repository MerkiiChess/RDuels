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
public class ArenaModel implements Cloneable {

    private final String arenaName;
    private final String displayName;
    private final Material material;
    @Setter
    private EntityPosition onePosition;
    @Setter
    private EntityPosition twoPosition;
    @Setter
    private EntityPosition spectatorPosition;
    @Setter
    private Map<Integer, EntityPosition> ffaPositions;
    @Setter
    private EntityPosition schematicPosition;
    private boolean customKits;
    private List<String> customKitsName;
    private final boolean ffa;
    private final boolean breaking;
    private final String schematic;
    private final int radiusDeleteBlocks;

    public static ArenaModel create(String arenaName, String displayName, Material material, EntityPosition onePosition, EntityPosition twoPosition, EntityPosition spectatorPosition, boolean ffa, boolean breaking, String schematic) {
        return new ArenaModel(arenaName, displayName, material, onePosition, twoPosition, spectatorPosition, new HashMap<Integer, EntityPosition>(), null, false, new ArrayList<String>(), ffa, breaking, schematic, 50);
    }

    public static ArenaModel create(String arenaName, String displayName, Material material, EntityPosition onePosition, EntityPosition twoPosition, EntityPosition spectatorPosition) {
        return new ArenaModel(arenaName, displayName, material, onePosition, twoPosition, spectatorPosition, new HashMap<Integer, EntityPosition>(), null, false, new ArrayList<String>(), false, false, "NO_SCHEMATIC", 50);
    }

    public ArenaModel clone() {
        try {
            return (ArenaModel)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    ArenaModel(String arenaName, String displayName, Material material, EntityPosition onePosition, EntityPosition twoPosition, EntityPosition spectatorPosition, Map<Integer, EntityPosition> ffaPositions, EntityPosition schematicPosition, boolean customKits, List<String> customKitsName, boolean ffa, boolean breaking, String schematic, int radiusDeleteBlocks) {
        this.arenaName = arenaName;
        this.displayName = displayName;
        this.material = material;
        this.onePosition = onePosition;
        this.twoPosition = twoPosition;
        this.spectatorPosition = spectatorPosition;
        this.ffaPositions = ffaPositions;
        this.schematicPosition = schematicPosition;
        this.customKits = customKits;
        this.customKitsName = customKitsName;
        this.ffa = ffa;
        this.breaking = breaking;
        this.schematic = schematic;
        this.radiusDeleteBlocks = radiusDeleteBlocks;
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
            return new ArenaModel(this.arenaName, this.displayName, this.material, this.onePosition, this.twoPosition, this.spectatorPosition, this.ffaPositions, this.schematicPosition, this.customKits, this.customKitsName, this.ffa, this.breaking, this.schematic, this.radiusDeleteBlocks);
        }

        public String toString() {
            return "ArenaModel.ArenaModelBuilder(arenaName=" + this.arenaName + ", displayName=" + this.displayName + ", material=" + this.material + ", onePosition=" + this.onePosition + ", twoPosition=" + this.twoPosition + ", spectatorPosition=" + this.spectatorPosition + ", ffaPositions=" + this.ffaPositions + ", schematicPosition=" + this.schematicPosition + ", customKits=" + this.customKits + ", customKitsName=" + this.customKitsName + ", ffa=" + this.ffa + ", breaking=" + this.breaking + ", schematic=" + this.schematic + ", radiusDeleteBlocks=" + this.radiusDeleteBlocks + ")";
        }
    }
}

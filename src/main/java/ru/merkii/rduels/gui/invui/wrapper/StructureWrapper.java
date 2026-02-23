package ru.merkii.rduels.gui.invui.wrapper;

import xyz.xenondevs.invui.gui.structure.Structure;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StructureWrapper {

    private static final Field STRUCTURE_DATA_FIELD;

    static {
        try {
            STRUCTURE_DATA_FIELD = Structure.class.getDeclaredField("structureData");
            STRUCTURE_DATA_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final Structure structure;
    private final String structureData;

    public StructureWrapper(Structure structure) {
        this.structure = structure;
        try {
            this.structureData = (String) STRUCTURE_DATA_FIELD.get(structure);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getSlotIndexes(char key) {
        List<Integer> slotIndexes = new ArrayList<>();

        int index = structureData.indexOf(key);
        while (index >= 0) {
            slotIndexes.add(index);
            index = structureData.indexOf(key, index + 1);
        }
        return slotIndexes;
    }

    public Structure getStructure() {
        return structure;
    }

}

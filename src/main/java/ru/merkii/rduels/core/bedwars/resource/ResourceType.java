package ru.merkii.rduels.core.bedwars.resource;

import org.bukkit.Material;

/** Bedwars currencies, mapped to their in-world item. */
public enum ResourceType {

    IRON(Material.IRON_INGOT),
    GOLD(Material.GOLD_INGOT),
    DIAMOND(Material.DIAMOND),
    EMERALD(Material.EMERALD);

    private final Material material;

    ResourceType(Material material) {
        this.material = material;
    }

    public Material material() {
        return material;
    }

    public static ResourceType fromName(String name) {
        if (name == null) {
            return null;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}

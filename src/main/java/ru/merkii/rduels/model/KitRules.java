package ru.merkii.rduels.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Per-kit gameplay rules (see the {@code rules} section of kits.yml). Every field has a
 * vanilla-neutral default, so kits without a rules section behave exactly as before.
 */
@Getter
@AllArgsConstructor
public class KitRules {

    public static final KitRules DEFAULT = new KitRules(20.0D, 1.0D, false, false, -1, Map.of(), 0, null);

    /** Max health in HP (2 per heart). Vanilla default is 20. */
    private final double maxHealth;
    /** Outgoing damage of fighters is multiplied by this value. */
    private final double damageMultiplier;
    /** UHC mode: hunger never drains during the fight. */
    private final boolean noHunger;
    /** Crafting is blocked during the fight. */
    private final boolean disableCrafting;
    /** Max build height in blocks above the arena spawn; -1 = unlimited. */
    private final int buildHeight;
    /** Per-item use cooldowns in seconds (e.g. GOLDEN_APPLE, ENDER_PEARL). */
    private final Map<Material, Integer> itemCooldowns;
    /** Placed blocks disappear after this many seconds; 0 = off. */
    private final int blockDecaySeconds;
    /** Dye colour applied to leather armour pieces of the kit; null = leave as-is. */
    @Nullable
    private final Color armorColor;

    public boolean hasArmorColor() {
        return armorColor != null;
    }

    public boolean hasCustomMaxHealth() {
        return maxHealth != 20.0D;
    }

    public boolean hasDamageMultiplier() {
        return damageMultiplier != 1.0D;
    }

    public boolean hasBuildHeight() {
        return buildHeight >= 0;
    }

    public boolean hasBlockDecay() {
        return blockDecaySeconds > 0;
    }

}

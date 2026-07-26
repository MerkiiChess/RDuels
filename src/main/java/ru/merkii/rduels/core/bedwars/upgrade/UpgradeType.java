package ru.merkii.rduels.core.bedwars.upgrade;

/**
 * Team-wide Bedwars upgrades. Effects are applied in code (see the loadout/upgrade
 * services); per-level costs and values come from bedwars-upgrades.yml.
 */
public enum UpgradeType {

    /** Sharpness enchant on team swords. */
    SHARPNESS,
    /** Protection enchant on team armour. */
    PROTECTION,
    /** Permanent Haste effect for team members. */
    HASTE,
    /** Faster team-base resource generators (forge). */
    FORGE,
    /** Regeneration while near the team bed (heal pool). */
    HEAL_POOL;

    public static UpgradeType fromName(String name) {
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

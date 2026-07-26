package ru.merkii.rduels.core.bedwars.game;

import org.bukkit.Material;

/**
 * Per-player purchased gear that persists across Bedwars respawns (armour tier and
 * tools), exactly like the reference game where only the sword/blocks are lost on death.
 */
public class PlayerBuyState {

    /** Armour tier for leggings/boots: 0 leather, 1 chain, 2 iron, 3 diamond. */
    private int armorTier;
    private Material pickaxe;
    private int pickaxeTier;
    private Material axe;
    private int axeTier;
    private boolean shears;

    public int getArmorTier() {
        return armorTier;
    }

    public void upgradeArmor(int tier) {
        this.armorTier = Math.max(this.armorTier, tier);
    }

    public Material getPickaxe() {
        return pickaxe;
    }

    public void upgradePickaxe(Material material, int tier) {
        if (tier >= this.pickaxeTier) {
            this.pickaxe = material;
            this.pickaxeTier = tier;
        }
    }

    public Material getAxe() {
        return axe;
    }

    public void upgradeAxe(Material material, int tier) {
        if (tier >= this.axeTier) {
            this.axe = material;
            this.axeTier = tier;
        }
    }

    public boolean hasShears() {
        return shears;
    }

    public void giveShears() {
        this.shears = true;
    }
}

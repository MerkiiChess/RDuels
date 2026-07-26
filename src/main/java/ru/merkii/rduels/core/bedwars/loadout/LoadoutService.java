package ru.merkii.rduels.core.bedwars.loadout;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.merkii.rduels.core.bedwars.game.BedwarsGame;
import ru.merkii.rduels.core.bedwars.game.BedwarsTeam;
import ru.merkii.rduels.core.bedwars.game.PlayerBuyState;
import ru.merkii.rduels.core.bedwars.upgrade.UpgradeService;
import ru.merkii.rduels.core.bedwars.upgrade.UpgradeType;

/**
 * Builds a Bedwars player's loadout: a wooden sword, coloured leather helmet/chestplate,
 * tier-scaled leggings/boots, persistent tools, and the team's enchant/effect upgrades.
 * Used on spawn and on every respawn, and to re-apply upgrades when a team buys one.
 */
@Singleton
public class LoadoutService {

    private final UpgradeService upgradeService;

    @Inject
    public LoadoutService(UpgradeService upgradeService) {
        this.upgradeService = upgradeService;
    }

    /** Clears and rebuilds the player's full loadout (spawn / respawn). */
    public void give(BedwarsGame game, BedwarsTeam team, Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);

        int sharpness = upgradeService.isActive(team, UpgradeType.SHARPNESS)
                ? upgradeService.activeValue(team, UpgradeType.SHARPNESS) : 0;
        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        if (sharpness > 0) {
            enchant(sword, "sharpness", sharpness);
        }
        inventory.addItem(sword);

        PlayerBuyState buy = game.buyState(player.getUniqueId());
        giveTools(inventory, buy);
        equipArmor(player, team, buy);
        applyEffects(player, team);
    }

    /** Re-applies the team's enchant/effect upgrades to a member already in-game. */
    public void reapplyUpgrades(BedwarsTeam team, Player player) {
        int sharpness = upgradeService.isActive(team, UpgradeType.SHARPNESS)
                ? upgradeService.activeValue(team, UpgradeType.SHARPNESS) : 0;
        int protection = upgradeService.isActive(team, UpgradeType.PROTECTION)
                ? upgradeService.activeValue(team, UpgradeType.PROTECTION) : 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType().name().endsWith("_SWORD") && sharpness > 0) {
                enchant(item, "sharpness", sharpness);
            }
        }
        if (protection > 0) {
            for (ItemStack piece : player.getInventory().getArmorContents()) {
                if (piece != null) {
                    enchant(piece, "protection", protection);
                }
            }
        }
        applyEffects(player, team);
    }

    private void giveTools(PlayerInventory inventory, PlayerBuyState buy) {
        if (buy.getPickaxe() != null) {
            inventory.addItem(new ItemStack(buy.getPickaxe()));
        }
        if (buy.getAxe() != null) {
            inventory.addItem(new ItemStack(buy.getAxe()));
        }
        if (buy.hasShears()) {
            inventory.addItem(new ItemStack(Material.SHEARS));
        }
    }

    private void equipArmor(Player player, BedwarsTeam team, PlayerBuyState buy) {
        PlayerInventory inventory = player.getInventory();
        int protection = upgradeService.isActive(team, UpgradeType.PROTECTION)
                ? upgradeService.activeValue(team, UpgradeType.PROTECTION) : 0;
        // Helmet + chestplate stay coloured leather; leggings + boots scale with the tier.
        inventory.setHelmet(protect(colored(Material.LEATHER_HELMET, team.getArmorColor()), protection));
        inventory.setChestplate(protect(colored(Material.LEATHER_CHESTPLATE, team.getArmorColor()), protection));
        inventory.setLeggings(protect(armorPiece(buy.getArmorTier(), "LEGGINGS", team.getArmorColor()), protection));
        inventory.setBoots(protect(armorPiece(buy.getArmorTier(), "BOOTS", team.getArmorColor()), protection));
    }

    private ItemStack armorPiece(int tier, String slot, Color color) {
        String prefix = switch (tier) {
            case 1 -> "CHAINMAIL";
            case 2 -> "IRON";
            case 3 -> "DIAMOND";
            default -> "LEATHER";
        };
        Material material = Material.getMaterial(prefix + "_" + slot);
        ItemStack piece = new ItemStack(material == null ? Material.LEATHER_BOOTS : material);
        return prefix.equals("LEATHER") ? colored(piece.getType(), color) : piece;
    }

    private ItemStack colored(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        if (item.getItemMeta() instanceof LeatherArmorMeta meta && color != null) {
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack protect(ItemStack piece, int level) {
        if (level > 0) {
            enchant(piece, "protection", level);
        }
        return piece;
    }

    private void applyEffects(Player player, BedwarsTeam team) {
        if (upgradeService.isActive(team, UpgradeType.HASTE)) {
            PotionEffectType haste = PotionEffectType.getByName("HASTE");
            if (haste != null) {
                int amplifier = Math.max(0, upgradeService.activeValue(team, UpgradeType.HASTE));
                player.addPotionEffect(new PotionEffect(haste, Integer.MAX_VALUE, amplifier, false, false));
            }
        }
    }

    private void enchant(ItemStack item, String key, int level) {
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
        if (enchantment != null && level > 0) {
            item.addUnsafeEnchantment(enchantment, level);
        }
    }
}

package ru.merkii.rduels.builder;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import ru.merkii.rduels.util.ColorUtil;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ItemBuilder implements Cloneable {

    private String material;
    private String basePotionEffect;
    private List<String> potionEffect;
    private int amount;
    private String displayName;
    private List<String> lore;
    private int slot;
    private ItemFlag[] itemFlags;
    private Map<String, Integer> enchants;

    public ItemBuilder() {
    }

    public static ItemBuilder builder() {
        return new ItemBuilder();
    }

    public ItemBuilder setMaterial(String material) {
        this.material = material;
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        this.material = material.name();
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder setLore(String... strings) {
        return this.setLore(Arrays.asList(strings));
    }

    public ItemBuilder addLore(List<String> list) {
        if (this.lore == null || this.lore.isEmpty()) {
            this.lore = new ArrayList<>();
        }
        this.lore.addAll(list);
        return this;
    }

    public ItemBuilder setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag[] itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public ItemBuilder addItemFlags(List<String> itemFlags) {
        this.itemFlags = itemFlags.stream()
                .map(ItemFlag::valueOf)
                .toArray(ItemFlag[]::new);
        return this;
    }

    public ItemBuilder replaceDisplayName(String old, String replace) {
        if (this.displayName != null) {
            this.displayName = this.displayName.replace(old, replace);
        }
        return this;
    }

    public ItemBuilder replaceLore(String old, String replace) {
        if (this.lore != null && !this.lore.isEmpty()) {
            this.lore = this.lore.stream().map(str -> str.replace(old, replace)).collect(Collectors.toList());
        }
        return this;
    }

    public ItemBuilder replaceLore(String old, int replace) {
        return this.replaceLore(old, String.valueOf(replace));
    }

    public ItemBuilder addEnchant(String keyEnchant, int lvl) {
        if (this.enchants == null) enchants = new HashMap<>();
        this.enchants.put(keyEnchant.toLowerCase(), lvl);
        return this;
    }

    public ItemBuilder setPotionEffect(String potionEffect) {
        this.basePotionEffect = potionEffect;
        return this;
    }

    public ItemBuilder setPotionEffects(List<String> potionEffects) {
        this.potionEffect = potionEffects;
        return this;
    }

    public ItemBuilder fromItemStack(ItemStack itemStack) {
        this.material = itemStack.getType().name();
        this.amount = itemStack.getAmount();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return this;
        }
        if ((itemStack.getType() == Material.POTION || itemStack.getType() == Material.SPLASH_POTION || itemStack.getType() == Material.TIPPED_ARROW) && meta instanceof PotionMeta potionMeta) {

            // Обработка базового эффекта зелья
            PotionType potionType = potionMeta.getBasePotionType();
            if (potionType != null) {
                String name = potionType.name();
                boolean extended = name.startsWith("LONG_");
                boolean upgraded = name.startsWith("STRONG_");
                String baseType = name.replace("LONG_", "").replace("STRONG_", "");
                this.basePotionEffect = baseType + ":" + extended + ":" + upgraded;
            }

            // Обработка кастомных эффектов зелья
            if (potionMeta.hasCustomEffects()) {
                if (this.potionEffect == null) {
                    this.potionEffect = new ArrayList<>();
                }
                this.potionEffect.clear(); // Очищаем список кастомных эффектов
                for (PotionEffect effect : potionMeta.getCustomEffects()) {
                    this.potionEffect.add(effect.getType().getName() + ":" + effect.getAmplifier() + ":" + effect.getDuration());
                }
            }
        }
        if (meta.getDisplayName() != null) {
            this.displayName = meta.getDisplayName();
        }
        if (meta.getLore() != null) {
            this.lore = meta.getLore();
        }
        if (meta.getItemFlags() != null) {
            this.itemFlags = meta.getItemFlags().toArray(new ItemFlag[0]);
        }
        if (meta.hasEnchants()) {
            this.enchants = new HashMap<>();
            meta.getEnchants().forEach((key, value) -> this.enchants.put(key.getKey().getKey(), value));
        }
        return this;
    }

    public ItemBuilder setEnchants(Map<String, Integer> enchants) {
        this.enchants = enchants;
        return this;
    }

    public ItemStack build() {
        if (this.material == null) {
            this.material = "AIR";
        }
        Material material = Material.getMaterial(this.material);
        if (material == null) {
            material = Material.SKELETON_SKULL;
        }
        ItemStack itemStack = new ItemStack(material, this.amount <= 0 ? 1 : this.amount);
        if (material == Material.POTION || material == Material.SPLASH_POTION || material == Material.TIPPED_ARROW) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            if (this.basePotionEffect != null) {
                String[] baseEffect = this.basePotionEffect.split(":");
                String type = baseEffect[0];
                boolean extended = Boolean.parseBoolean(baseEffect[1]);
                boolean upgraded = Boolean.parseBoolean(baseEffect[2]);
                String enumName = type;
                if (extended) {
                    enumName = "LONG_" + enumName;
                }
                if (upgraded) {
                    enumName = "STRONG_" + enumName;
                }
                PotionType potionType = PotionType.valueOf(enumName);
                potionMeta.setBasePotionType(potionType);
            }
            if (this.potionEffect != null && !this.potionEffect.isEmpty()) {
                this.potionEffect.stream().map(effect -> effect.split(":")).forEach(potionEffect -> potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(potionEffect[0]), Integer.parseInt(potionEffect[2]), Integer.parseInt(potionEffect[1])), false));
            }
            itemStack.setItemMeta(potionMeta);
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (this.displayName != null) {
            meta.setDisplayName(ColorUtil.color(this.displayName));
        }
        if (this.lore != null) {
            meta.setLore(ColorUtil.color(this.lore));
        }
        if (this.itemFlags != null) {
            meta.addItemFlags(this.itemFlags);
        }
        if (this.enchants != null && !this.enchants.isEmpty()) {
            this.enchants.forEach((key, value) -> {
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(key));
                if (ench != null) {
                    meta.addEnchant(ench, value, true);
                }
            });
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @NotNull
    public Material getMaterial() {
        return this.material == null || Material.getMaterial(this.material) == null ? Material.AIR : Material.getMaterial(this.material);
    }

    @Override
    public ItemBuilder clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (ItemBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
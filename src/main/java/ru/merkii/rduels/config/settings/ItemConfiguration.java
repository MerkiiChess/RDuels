package ru.merkii.rduels.config.settings;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Transient;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.builder.ItemBuilder;

import java.util.List;
import java.util.Map;

@ConfigInterface
public interface ItemConfiguration {

    String material();

    String basePotionEffect();

    List<String> potionEffects();

    int amount();

    String displayName();

    List<String> lore();

    int slot();

    List<String> itemFlags();

    Map<String, Integer> enchants();

    @Transient
    default ItemStack build() {
        return ItemBuilder.builder()
                .setMaterial(material())
                .setPotionEffect(basePotionEffect())
                .setPotionEffects(potionEffects())
                .setDisplayName(displayName())
                .setAmount(amount())
                .setLore(lore())
                .addItemFlags(itemFlags())
                .setEnchants(enchants()).build();

    }

}

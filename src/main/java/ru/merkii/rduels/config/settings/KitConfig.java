package ru.merkii.rduels.config.settings;

import lombok.Getter;
import org.bukkit.Material;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.model.KitModel;
import java.util.List;

@Getter
public class KitConfig extends Config {

    private List<KitModel> kits = fastList(new KitModel("Алмазка", 0, fastList("Алмазные вещи, 2 тотема"), Material.DIAMOND_CHESTPLATE, fastMap(fastList(1), fastList(ItemBuilder.builder().setMaterial(Material.DIAMOND_SWORD).addEnchant("DAMAGE_ALL", 5).addEnchant("FIRE_ASPECT", 2).addEnchant("MENDING", 1).addEnchant("DURABILITY", 3)))));

}

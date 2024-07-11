package ru.merkii.rduels.config.settings;

import lombok.Getter;
import org.bukkit.Material;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.model.KitModel;
import java.util.List;

@Getter
public class KitConfig extends Config {

    private List<KitModel> kits = KitConfig.fastList(new KitModel("Алмазка", 0, KitConfig.fastList("Алмазные вещи, 2 тотема"), Material.DIAMOND_CHESTPLATE, KitConfig.fastMap(KitConfig.fastList(1), KitConfig.fastList(ItemBuilder.builder().setMaterial(Material.DIAMOND_SWORD).addEnchant("DAMAGE_ALL", 5).addEnchant("FIRE_ASPECT", 2).addEnchant("MENDING", 1).addEnchant("DURABILITY", 3))), false, null));

}

package ru.merkii.rduels.config.enchant;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;

import java.util.List;

@ConfigInterface
public interface EnchantItemConfiguration {

    List<CustomKitEnchantCategory> enchants();

}

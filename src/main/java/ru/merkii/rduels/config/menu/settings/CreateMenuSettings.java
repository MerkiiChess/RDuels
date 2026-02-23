package ru.merkii.rduels.config.menu.settings;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;

import java.util.List;

@ConfigInterface
public interface CreateMenuSettings {

    List<CustomKitModel> kits();
    List<CustomKitCategory> categories();
    List<CustomKitEnchantCategory> customEnchants();

}

package ru.merkii.rduels.config.category;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;

import java.util.List;
import java.util.Map;

@ConfigInterface
public interface CategoryItemConfiguration {

    Map<String, CustomKitCategory> categories();

    void categories(Map<String, CustomKitCategory> categories);

}

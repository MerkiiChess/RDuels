package ru.merkii.rduels.core.customkit.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.config.settings.ItemConfiguration;

import java.util.Map;

@ConfigInterface
public interface CategoriesMenuConfiguration {

    Map<Integer, ItemConfiguration> itemStacks();

}

package ru.merkii.rduels.core.duel.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.config.settings.ItemConfiguration;
import ru.merkii.rduels.model.KitModel;

import java.util.Map;

@ConfigInterface
public interface RequestKitConfiguration {

    Map<ItemConfiguration, KitModel> kits();

}

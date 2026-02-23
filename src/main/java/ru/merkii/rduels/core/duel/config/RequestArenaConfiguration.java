package ru.merkii.rduels.core.duel.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.config.settings.ItemConfiguration;

import java.util.Map;

@ConfigInterface
public interface RequestArenaConfiguration {

    Map<ItemConfiguration, String> arenas();

}

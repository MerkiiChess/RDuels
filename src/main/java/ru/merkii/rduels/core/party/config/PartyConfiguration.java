package ru.merkii.rduels.core.party.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Setting;
import ru.merkii.rduels.config.settings.ItemConfiguration;

@ConfigInterface
public interface PartyConfiguration {

    int maxPartySize();

}


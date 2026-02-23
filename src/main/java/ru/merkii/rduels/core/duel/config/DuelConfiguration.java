package ru.merkii.rduels.core.duel.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Setting;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.settings.ItemConfiguration;
import ru.merkii.rduels.model.KitModel;

import java.util.Map;

@ConfigInterface
public interface DuelConfiguration {

    int itemRemoveSeconds();

    ChoiceKitMenuConfiguration choiceKitMenu();

    TitleSettingsConfiguration titleSettings();

}


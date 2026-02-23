package ru.merkii.rduels.core.duel.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Setting;

@ConfigInterface
public interface TitleSettingsConfiguration {

    ToFightConfiguration toFight();

    FightConfiguration fight();

    LoseConfiguration lose();

    WinConfiguration win();

}

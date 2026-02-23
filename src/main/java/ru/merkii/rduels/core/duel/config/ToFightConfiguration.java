package ru.merkii.rduels.core.duel.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Setting;

@ConfigInterface
public interface ToFightConfiguration {

    int fadeIn();

    int fadeOut();

    int stay();

    String text();

    String soundName();

    float v1();

    float v2();

}

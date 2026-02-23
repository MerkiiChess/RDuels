package ru.merkii.rduels.core.duel.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Setting;

@ConfigInterface
public interface LoseConfiguration {

    @Setting("fade-in")
    int fadeIn();

    @Setting("fade-out")
    int fadeOut();

    int stay();

    String text();

    @Setting("sound-name")
    String soundName();

    float v1();

    float v2();

}

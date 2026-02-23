package ru.merkii.rduels.core.duel.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Setting;

@ConfigInterface
public interface WinConfiguration {

    int fadeIn();

    int fadeOut();

    int stay();

    String text();

}

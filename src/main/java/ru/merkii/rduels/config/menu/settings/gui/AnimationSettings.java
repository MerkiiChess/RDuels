package ru.merkii.rduels.config.menu.settings.gui;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Transient;

import java.util.Map;

@ConfigInterface
public interface AnimationSettings {

    String type(); // TODO: Actually implement animation by specific type, currently unused

    Map<String, String> data();

    @Transient
    default String data(String key) {
        return data().get(key);
    }

}

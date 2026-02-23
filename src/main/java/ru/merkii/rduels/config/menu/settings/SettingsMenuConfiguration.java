package ru.merkii.rduels.config.menu.settings;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Transient;
import ru.merkii.rduels.config.menu.settings.format.FormatSettings;
import ru.merkii.rduels.config.menu.settings.gui.GuiSettings;
import ru.merkii.rduels.config.menu.settings.notification.NotificationSettings;

import java.util.Map;
import java.util.Optional;

@ConfigInterface
public interface SettingsMenuConfiguration {

    Map<String, GuiSettings> gui();

    NotificationSettings notification();

    FormatSettings format();

    CreateMenuSettings createSettings();

    @Transient
    default Optional<GuiSettings> gui(String key) {
        return Optional.ofNullable(gui().get(key));
    }

}

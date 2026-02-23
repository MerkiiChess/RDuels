package ru.merkii.rduels.config.menu;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.config.menu.messages.MessagesMenuConfiguration;
import ru.merkii.rduels.config.menu.settings.SettingsMenuConfiguration;

@ConfigInterface
public interface MenuConfiguration {

    SettingsMenuConfiguration settings();

    MessagesMenuConfiguration messages();

}

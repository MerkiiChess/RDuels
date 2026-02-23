package ru.merkii.rduels.config.menu.settings.gui;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.config.model.ExecuteCommand;

import java.util.List;

@ConfigInterface
public interface ClickSettings {

    String open();

    String callback();

    List<ExecuteCommand> executeCommands();

}
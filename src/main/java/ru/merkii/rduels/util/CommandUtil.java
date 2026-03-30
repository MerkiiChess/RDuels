package ru.merkii.rduels.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CommandUtil {

    public String getOriginalCommand(@NotNull String fullCmd) {
        if (fullCmd.length() < 2) return fullCmd;
        String input = fullCmd.startsWith("/") ? fullCmd.substring(1) : fullCmd;
        String label = input.split(" ")[0].toLowerCase();
        int colonIndex = label.indexOf(':');
        if (colonIndex != -1) {
            label = label.substring(colonIndex + 1);
        }
        Command command = Bukkit.getCommandMap().getCommand(label);
        return (command != null) ? command.getName() : label;
    }
}
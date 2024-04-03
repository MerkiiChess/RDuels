package ru.merkii.rduels.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;

public class CommandUtil {

    public static String getOriginalCommand(String fullCmd) {
        String first = fullCmd.toLowerCase().split(" ")[0];
        if (first.contains(":")) {
            first = first.split(":")[1];
        }
        Command command = null;
        try {
            SimpleCommandMap scm = (SimpleCommandMap)getFromField(Bukkit.getServer(), "commandMap");
            command = scm.getCommand(first.substring(1));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        if (command != null) {
            return command.getName().replace("/", "");
        }
        if (fullCmd.length() < 2) {
            return fullCmd;
        }
        return fullCmd.substring(2);
    }

    public static Object getFromField(Object instance, String field) throws ReflectiveOperationException {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(instance);
    }

}

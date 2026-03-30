package ru.merkii.rduels.config.model;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.EnumSet;

public class ExecuteCommand {

    private static final EnumSet<ExecutorType> EXECUTOR_TYPES = EnumSet.allOf(ExecutorType.class);
    private final ExecutorType executorType;
    private final String command;

    public ExecuteCommand(String input) {
        this.executorType = EXECUTOR_TYPES.stream().filter(type -> input.startsWith(type.prefix)).findFirst().orElse(ExecutorType.CONSOLE);
        String strippedInput = input.replace(executorType.prefix, "");
        this.command = strippedInput.trim();
    }

    public void execute(Player target) {
        String execution = applyPlaceholders(target, command);
        executorType.execute(target, execution);
    }

    public String command() {
        return command;
    }

    public String prefix() {
        return executorType.prefix;
    }

    private String applyPlaceholders(Player target, String rawCommand) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return rawCommand;
        }
        try {
            Class<?> placeholderApiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Method method = placeholderApiClass.getMethod("setPlaceholders", Player.class, String.class);
            Object result = method.invoke(null, target, rawCommand);
            return result instanceof String stringResult ? stringResult : rawCommand;
        } catch (ReflectiveOperationException exception) {
            return rawCommand;
        }
    }

    private enum ExecutorType {
        PLAYER("[player]") {
            @Override
            public void execute(Player target, String execution) {
                target.chat(execution);
            }
        }, CONSOLE("[console]") {
            @Override
            public void execute(Player target, String execution) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), execution);
            }
        };
        final String prefix;

        ExecutorType(String prefix) {
            this.prefix = prefix;
        }

        public abstract void execute(Player target, String execution);
    }

}

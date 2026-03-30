package ru.merkii.rduels;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.Listener;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.command.DayCommand;
import ru.merkii.rduels.command.NightCommand;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.manager.DatabaseManager;
import ru.merkii.rduels.util.PluginConsole;

import java.util.Arrays;
import java.util.List;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PluginBootstrap {

    Lamp<BukkitCommandActor> lamp;
    MessageConfig messageConfig;
    DuelAPI duelAPI;
    List<Core> cores;
    List<Listener> listeners;
    List<Object> commands;

    @Inject
    public PluginBootstrap(MessageConfig messageConfig, DuelAPI duelAPI, List<Core> cores, List<Listener> listeners, Lamp<BukkitCommandActor> lamp, DayCommand dayCommand, NightCommand nightCommand) {
        this.lamp = lamp;
        this.messageConfig = messageConfig;
        this.duelAPI = duelAPI;
        this.cores = cores;
        this.listeners = listeners;
        this.commands = Arrays.asList(
                dayCommand,
                nightCommand
        );
    }

    public void initialize(RDuels plugin) {
        commands.forEach(lamp::register);

        for (Core core : cores) {
            core.enable(plugin);
            plugin.getCores().add(core);
        }

        var pm = plugin.getServer().getPluginManager();
        listeners.forEach(listener -> pm.registerEvents(listener, plugin));

        if (pm.isPluginEnabled("PlaceholderAPI")) {
            plugin.getLogger().info("Регистрирую PlaceholderAPI placeholders.");
            new ru.merkii.rduels.placeholder.DuelPAPIHook(messageConfig, duelAPI).register();
        } else {
            PluginConsole.info(plugin, "PlaceholderAPI не найден. Плейсхолдеры RDuels отключены.");
        }
    }
}

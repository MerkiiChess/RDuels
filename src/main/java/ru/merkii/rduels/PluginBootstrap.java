package ru.merkii.rduels;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.event.Listener;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.command.DayCommand;
import ru.merkii.rduels.command.NightCommand;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.manager.DatabaseManager;

import java.util.Arrays;
import java.util.List;

@Singleton
public class PluginBootstrap {

    private final Lamp<BukkitCommandActor> lamp;
    private final DatabaseManager databaseManager;
    private final List<Core> cores;
    private final List<Listener> listeners;
    private final List<Object> commands;

    @Inject
    public PluginBootstrap(DatabaseManager databaseManager, List<Core> cores, List<Listener> listeners, Lamp<BukkitCommandActor> lamp, DayCommand dayCommand, NightCommand nightCommand) {
        this.lamp = lamp;
        this.databaseManager = databaseManager;
        this.cores = cores;
        this.listeners = listeners;
        this.commands = Arrays.asList(
                dayCommand,
                nightCommand
        );
    }

    public void initialize(RDuels plugin) {
        databaseManager.createTable().join();

        commands.forEach(lamp::register);

        for (Core core : cores) {
            core.enable(plugin);
            plugin.getCores().add(core);
        }

        var pm = plugin.getServer().getPluginManager();
        listeners.forEach(listener -> pm.registerEvents(listener, plugin));
    }
}
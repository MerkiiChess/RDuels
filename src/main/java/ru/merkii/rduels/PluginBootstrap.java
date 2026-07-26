package ru.merkii.rduels;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.command.DayCommand;
import ru.merkii.rduels.command.NightCommand;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.statistic.StatisticFlushScheduler;
import ru.merkii.rduels.statistic.StatisticService;

import java.util.Arrays;
import java.util.List;

@Singleton
public class PluginBootstrap {

    private final Lamp<BukkitCommandActor> lamp;
    private final StatisticService statisticService;
    private final List<Core> cores;
    private final List<Listener> listeners;
    private final List<Object> commands;

    @Inject
    public PluginBootstrap(StatisticService statisticService, List<Core> cores, List<Listener> listeners, Lamp<BukkitCommandActor> lamp, DayCommand dayCommand, NightCommand nightCommand) {
        this.lamp = lamp;
        this.statisticService = statisticService;
        this.cores = cores;
        this.listeners = listeners;
        this.commands = Arrays.asList(
                dayCommand,
                nightCommand
        );
    }

    public void initialize(RDuels plugin) {
        // Tables are created eagerly by Ebean DDL when the Database bean is built
        // (see DatabaseFactory / Config#createAll), so no explicit table creation is needed here.
        commands.forEach(lamp::register);

        for (Core core : cores) {
            core.enable(plugin);
            plugin.getCores().add(core);
        }

        // Single registration point for ALL listeners: every @Singleton bean implementing
        // Listener is auto-discovered here. Cores must NOT register their listeners
        // themselves, or events would be handled twice.
        var pm = plugin.getServer().getPluginManager();
        listeners.forEach(listener -> pm.registerEvents(listener, plugin));

        // Warm the statistic cache for players already online (e.g. after a /reload)
        // and start the periodic async flush task.
        Bukkit.getOnlinePlayers().forEach(player -> statisticService.load(player.getUniqueId()));
        new StatisticFlushScheduler(statisticService).start(plugin);
    }
}
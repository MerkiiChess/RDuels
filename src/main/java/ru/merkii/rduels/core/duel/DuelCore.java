package ru.merkii.rduels.core.duel;

import io.avaje.inject.BeanScope;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.command.DuelCommand;
import ru.merkii.rduels.core.duel.command.LeaveCommand;
import ru.merkii.rduels.core.duel.command.RDuelCommand;
import ru.merkii.rduels.core.duel.command.SpectatorCommand;
import ru.merkii.rduels.core.duel.listener.DuelListener;
import ru.merkii.rduels.core.duel.schedualer.DuelSpectatorScheduler;

@Getter
@Singleton
public class DuelCore implements Core {

    private DuelSpectatorScheduler duelSpectatorScheduler;
    private final Lamp<BukkitCommandActor> lamp;

    @Inject
    public DuelCore(Lamp<BukkitCommandActor> lamp) {
        this.lamp = lamp;
    }

    @Override
    public void enable(RDuels plugin) {
        reloadConfig(plugin);
        plugin.registerListeners(DuelListener.class);
        BeanScope beanScope = RDuels.beanScope();
        registerCommands(
                beanScope.get(DuelCommand.class),
                beanScope.get(LeaveCommand.class),
                beanScope.get(RDuelCommand.class),
                beanScope.get(SpectatorCommand.class)
        );
        this.duelSpectatorScheduler = DuelSpectatorScheduler.start();
    }

    @Override
    public void disable(RDuels plugin) {
        DuelAPI duelAPI = RDuels.beanScope().get(DuelAPI.class);
        plugin.getServer().getOnlinePlayers().stream()
                .map(BukkitAdapter::adapt)
                .filter(duelAPI::isFightPlayer)
                .forEach(player -> duelAPI.stopFight(duelAPI.getFightModelFromPlayer(player), null, null));
        this.duelSpectatorScheduler.stop();
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }

    private void registerCommands(Object... objects) {
        for (Object object : objects) {
            lamp.register(object);
        }
    }
}

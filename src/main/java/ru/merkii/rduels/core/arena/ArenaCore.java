package ru.merkii.rduels.core.arena;

import jakarta.inject.Singleton;
import lombok.Getter;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.arena.listener.BlockListener;

@Getter
@Singleton
public class ArenaCore implements Core {

    @Override
    public void enable(RDuels plugin) {
        this.reloadConfig(plugin);
        plugin.registerListeners(BlockListener.class);
    }

    @Override
    public void disable(RDuels plugin) {
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }

}

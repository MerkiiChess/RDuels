package ru.merkii.rduels.core.arena;

import lombok.Getter;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.api.provider.ArenaAPIProvider;
import ru.merkii.rduels.core.arena.bucket.ArenaBlockBuildBucket;
import ru.merkii.rduels.core.arena.bucket.ArenaBusyBucket;
import ru.merkii.rduels.core.arena.config.ArenaSettings;
import ru.merkii.rduels.core.arena.listener.BlockListener;
import ru.merkii.rduels.core.arena.model.ArenaModel;

import java.util.stream.Collectors;

@Getter
public class ArenaCore implements Core {

    public static ArenaCore INSTANCE;
    private ArenaBusyBucket arenaBusyBucket;
    private ArenaSettings arenas;
    private ArenaAPI arenaAPI;
    private ArenaBlockBuildBucket arenaBlockBuildBucket;

    @Override
    public void enable(RDuels plugin) {
        INSTANCE = this;
        this.arenaBlockBuildBucket = new ArenaBlockBuildBucket();
        this.reloadConfig(plugin);
        plugin.getPaperCommandManager().getCommandCompletions().registerCompletion("arenas", c -> this.arenas.getArenas().stream().map(ArenaModel::getArenaName).collect(Collectors.toList()));
        this.arenaBusyBucket = new ArenaBusyBucket();
        this.arenaAPI = new ArenaAPIProvider();
        plugin.registerListeners(new BlockListener());
    }

    @Override
    public void disable(RDuels plugin) {
    }

    @Override
    public void reloadConfig(RDuels plugin) {
        this.arenas = plugin.loadSettings("arenas.json", ArenaSettings.class);
    }

}

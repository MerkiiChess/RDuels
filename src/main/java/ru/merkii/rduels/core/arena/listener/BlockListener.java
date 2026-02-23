package ru.merkii.rduels.core.arena.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.arena.bucket.ArenaBlockBuildBucket;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

@Singleton
public class BlockListener implements Listener {

    private final DuelAPI duelAPI;
    private final ArenaBlockBuildBucket arenaBlockBuildBucket;

    @Inject
    public BlockListener(DuelAPI duelAPI, ArenaBlockBuildBucket arenaBlockBuildBucket) {
        this.duelAPI = duelAPI;
        this.arenaBlockBuildBucket = arenaBlockBuildBucket;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player bukkitPlayer = event.getPlayer();
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        if (!duelAPI.isFightPlayer(player)) {
            return;
        }
        DuelFightModel duelFightModel = duelAPI.getFightModelFromPlayer(player);
        if (duelFightModel == null) {
            return;
        }
        ArenaModel arenaModel = duelFightModel.getArenaModel();
        if (!arenaModel.isBreaking()) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        if (this.arenaBlockBuildBucket.isExistsBlock(arenaModel, block)) {
            this.arenaBlockBuildBucket.removeBlock(arenaModel, block);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player bukkitPlayer = event.getPlayer();
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        if (!duelAPI.isFightPlayer(player)) {
            return;
        }
        DuelFightModel duelFightModel = duelAPI.getFightModelFromPlayer(player);
        if (duelFightModel == null) {
            return;
        }
        if (!duelFightModel.getArenaModel().isBreaking()) {
            event.setCancelled(true);
            return;
        }
        this.arenaBlockBuildBucket.addBlock(duelFightModel.getArenaModel(), event.getBlock());
    }

}

package ru.merkii.rduels.core.arena.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.arena.bucket.ArenaBlockBuildBucket;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

public class BlockListener implements Listener {

    private final ArenaBlockBuildBucket arenaBlockBuildBucket = ArenaCore.INSTANCE.getArenaBlockBuildBucket();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!DuelCore.INSTANCE.getDuelAPI().isFightPlayer(player)) {
            return;
        }
        DuelFightModel duelFightModel = DuelCore.INSTANCE.getDuelAPI().getFightModelFromPlayer(player);
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
        Player player = event.getPlayer();
        if (!DuelCore.INSTANCE.getDuelAPI().isFightPlayer(player)) {
            return;
        }
        DuelFightModel duelFightModel = DuelCore.INSTANCE.getDuelAPI().getFightModelFromPlayer(player);
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

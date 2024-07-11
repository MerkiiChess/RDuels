package ru.merkii.rduels.core.arena.bucket;

import org.bukkit.block.Block;
import ru.merkii.rduels.core.arena.model.ArenaModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaBlockBuildBucket {

    private final Map<ArenaModel, List<Block>> blocksBuild = new HashMap<ArenaModel, List<Block>>();

    public void addBlock(ArenaModel arenaModel, Block block) {
        if (!this.blocksBuild.containsKey(arenaModel)) {
            this.blocksBuild.put(arenaModel, new ArrayList<>());
        }
        List<Block> blocks = this.blocksBuild.get(arenaModel);
        blocks.add(block);
        this.blocksBuild.replace(arenaModel, blocks);
    }

    public void removeBlock(ArenaModel arenaModel, Block block) {
        if (!this.blocksBuild.containsKey(arenaModel)) {
            this.blocksBuild.put(arenaModel, new ArrayList<>());
            return;
        }
        List<Block> blocks = this.blocksBuild.get(arenaModel);
        blocks.remove(block);
        this.blocksBuild.replace(arenaModel, blocks);
    }

    public List<Block> getAllBlocks(ArenaModel arenaModel) {
        return this.blocksBuild.getOrDefault(arenaModel, new ArrayList<>());
    }

    public boolean isExistsBlock(ArenaModel arenaModel, Block block) {
        if (!this.blocksBuild.containsKey(arenaModel)) {
            return false;
        }
        return !this.blocksBuild.get(arenaModel).isEmpty() && this.blocksBuild.get(arenaModel).contains(block);
    }

}

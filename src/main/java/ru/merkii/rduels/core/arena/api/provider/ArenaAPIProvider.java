package ru.merkii.rduels.core.arena.api.provider;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.bucket.ArenaBlockBuildBucket;
import ru.merkii.rduels.core.arena.bucket.ArenaBusyBucket;
import ru.merkii.rduels.core.arena.config.ArenaSettings;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.KitModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArenaAPIProvider implements ArenaAPI {

    private final ArenaCore arenaCore = ArenaCore.INSTANCE;
    private final ArenaBlockBuildBucket arenaBlockBuildBucket = this.arenaCore.getArenaBlockBuildBucket();
    private final ArenaSettings arenaSettings = this.arenaCore.getArenas();
    private final ArenaBusyBucket arenaBusyBucket = this.arenaCore.getArenaBusyBucket();

    @Override
    public ArenaModel getArenaFromName(String name) {
        return this.arenaSettings.getArenas().stream().filter(arenaModel -> arenaModel.getArenaName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public ArenaModel getArenaFromDisplayName(String displayName) {
        return this.arenaSettings.getArenas().stream().filter(arenaModel -> arenaModel.getDisplayName().equalsIgnoreCase(displayName)).findFirst().orElse(null);
    }

    @Override
    public boolean isContainsArena(String name) {
        return this.arenaSettings.getArenas().stream().anyMatch(arenaModel -> arenaModel.getArenaName().equalsIgnoreCase(name));
    }

    @Override
    public List<ArenaModel> getArenasFromName(String name) {
        return this.arenaSettings.getArenas().stream().filter(arenaModel -> arenaModel.getDisplayName().equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    @Override
    public void addBusyArena(ArenaModel arenaModel) {
        this.arenaBusyBucket.add(arenaModel);
    }

    @Override
    public void removeBusyArena(ArenaModel arenaModel) {
        this.arenaBusyBucket.remove(arenaModel);
    }

    @Override
    public boolean isBusyArena(ArenaModel arenaModel) {
        return this.arenaBusyBucket.getArenas().stream().anyMatch(arenaModel::equals);
    }

    @Override
    public void restoreArena(ArenaModel arenaModel) {
        EntityPosition position;
        block11: {
            position = arenaModel.getSchematicPosition();
            if (position == null) {
                RDuels.getInstance().getLogger().info("Позиция схематики у арены: " + arenaModel.getDisplayName() + " не установлена!");
                return;
            }
            try {
                List<Block> blocks = this.arenaBlockBuildBucket.getAllBlocks(arenaModel);
                if (!blocks.isEmpty()) {
                    blocks.stream().filter(Objects::nonNull).forEach(block -> block.setType(Material.AIR));
                    blocks.clear();
                }
            }
            catch (NullPointerException ignored) {
                List<Block> blocks = ArenaCore.INSTANCE.getArenaBlockBuildBucket().getAllBlocks(arenaModel);
                if (blocks.isEmpty()) break block11;
                blocks.stream().filter(Objects::nonNull).forEach(block -> block.setType(Material.AIR));
                blocks.clear();
            }
        }
        Location location = position.toLocation();
        int radius = arenaModel.getRadiusDeleteBlocks();
        for (int x = location.getBlockX() - radius; x < location.getBlockX() + radius; ++x) {
            for (int y = location.getBlockY() - radius; y < location.getBlockY() + radius; ++y) {
                for (int z = location.getBlockZ() - radius; z < location.getBlockY() + radius; ++z) {
                    Location newLocation = new Location(location.getWorld(), x, y, z);
                    newLocation.getBlock().setType(Material.STONE);
                    newLocation.getBlock().setType(Material.AIR);
                }
            }
        }
        File file = new File(RDuels.getInstance().getDataFolder() + "/schematic", arenaModel.getSchematic());
        if (!file.exists()) {
            RDuels.getInstance().getLogger().info("Схематика: " + arenaModel.getSchematic() + " не найдена!");
            return;
        }
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try {
            if (format == null) {
                RDuels.getInstance().debug("ClipboardFormat exception: Not found file " + file.getName());
                return;
            }
            ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()));
            Clipboard clipboard = reader.read();
            EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(location.getWorld())).build();
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ())).ignoreAirBlocks(false).build();
            Operations.complete(operation);
            editSession.getChangeSet().setRecordChanges(true);
            editSession.close();
        } catch (WorldEditException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ArenaModel> getArenaFromKit(KitModel kitModel) {
        return this.arenaSettings.getArenas().stream().filter(ArenaModel::isCustomKits).filter(Objects::nonNull).filter(arena -> arena.getCustomKitsName() != null).filter(arena -> !arena.getCustomKitsName().isEmpty()).filter(arena -> arena.getCustomKitsName().contains(kitModel.getDisplayName())).filter(arena -> !this.isBusyArena(arena)).findFirst();
    }


}

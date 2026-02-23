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
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.bucket.ArenaBlockBuildBucket;
import ru.merkii.rduels.core.arena.bucket.ArenaBusyBucket;
import ru.merkii.rduels.core.arena.config.ArenaConfiguration;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.KitModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Singleton
public class ArenaAPIProvider implements ArenaAPI {

    private final ArenaBlockBuildBucket arenaBlockBuildBucket;
    private final ArenaConfiguration arenaConfiguration;
    private final ArenaBusyBucket arenaBusyBucket;

    @Inject
    public ArenaAPIProvider(ArenaBlockBuildBucket arenaBlockBuildBucket, ArenaConfiguration arenaConfiguration, ArenaBusyBucket arenaBusyBucket) {
        this.arenaBlockBuildBucket = arenaBlockBuildBucket;
        this.arenaConfiguration = arenaConfiguration;
        this.arenaBusyBucket = arenaBusyBucket;
    }

    @Override
    @Nullable
    public ArenaModel getFreeArena() {
        List<ArenaModel> availableArenas = arenaConfiguration.arenas().keySet().stream()
                .filter(arena -> !arena.isFfa())
                .filter(arena -> !arena.isCustomKits())
                .filter(arena -> !arenaBusyBucket.getArenas().contains(arena))
                .toList();
        return availableArenas.isEmpty() ? null : availableArenas.get(ThreadLocalRandom.current().nextInt(availableArenas.size()));
    }

    @Override
    @Nullable
    public ArenaModel getFreeArenaName(String name) {
        return getArenasFromName(name).stream()
                .filter(arena -> !arenaBusyBucket.getArenas().contains(arena))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Nullable
    public ArenaModel getFreeArenaFFA() {
        List<ArenaModel> availableArenas = this.arenaConfiguration.arenas().keySet().stream()
                .filter(ArenaModel::isFfa)
                .filter(arena -> !arenaBusyBucket.getArenas().contains(arena))
                .toList();
        return availableArenas.isEmpty() ? null : availableArenas.get(ThreadLocalRandom.current().nextInt(availableArenas.size()));
    }

    @Override
    public ArenaModel getArenaFromName(String name) {
        return this.arenaConfiguration.arenas()
                .keySet()
                .stream()
                .filter(arenaModel -> arenaModel.getArenaName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ArenaModel getArenaFromDisplayName(String displayName) {
        return this.arenaConfiguration.arenas().keySet().stream().filter(arenaModel -> arenaModel.getDisplayName().equalsIgnoreCase(displayName)).findFirst().orElse(null);
    }

    @Override
    public boolean isContainsArena(String name) {
        return this.arenaConfiguration.arenas().keySet().stream().anyMatch(arenaModel -> arenaModel.getArenaName().equalsIgnoreCase(name));
    }

    @Override
    public List<ArenaModel> getArenasFromName(String name) {
        return this.arenaConfiguration.arenas().keySet().stream().filter(arenaModel -> arenaModel.getDisplayName().equalsIgnoreCase(name)).collect(Collectors.toList());
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
            } catch (NullPointerException ignored) {
                List<Block> blocks = arenaBlockBuildBucket.getAllBlocks(arenaModel);
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
                    newLocation.getBlock().setType(Material.AIR);
                }
            }
        }
        File file = new File(RDuels.getInstance().getDataFolder() + "/schematic", arenaModel.getSchematic());
        if (!file.exists()) {
            RDuels.getInstance().getLogger().warning("Схематика: " + arenaModel.getSchematic() + " не найдена!");
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
        return this.arenaConfiguration.arenas().keySet().stream().filter(ArenaModel::isCustomKits).filter(arena -> arena.getCustomKitsName() != null).filter(arena -> !arena.getCustomKitsName().isEmpty()).filter(arena -> arena.getCustomKitsName().contains(kitModel.getDisplayName())).filter(arena -> !this.isBusyArena(arena)).findFirst();
    }


}

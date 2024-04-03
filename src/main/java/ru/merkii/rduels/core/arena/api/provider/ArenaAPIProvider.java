package ru.merkii.rduels.core.arena.api.provider;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.bucket.ArenaBusyBucket;
import ru.merkii.rduels.core.arena.config.ArenaSettings;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaAPIProvider implements ArenaAPI {

    private final ArenaCore arenaCore = ArenaCore.INSTANCE;
    private final ArenaSettings arenaSettings = arenaCore.getArenas();
    private final ArenaBusyBucket arenaBusyBucket = arenaCore.getArenaBusyBucket();

    @Override
    public @Nullable ArenaModel getArenaFromName(String name) {
        return this.arenaSettings.getArenas().stream().filter(arenaModel -> arenaModel.getArenaName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public @Nullable ArenaModel getArenaFromDisplayName(String displayName) {
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
        File file = new File(RDuels.getInstance().getDataFolder() + "/schematic", arenaModel.getSchematic());
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try {
            if (format == null) {
                RDuels.getInstance().debug("ClipboardFormat exception: Not found file " + file.getName());
                return;
            }
            try (ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()))) {
                reader.read();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

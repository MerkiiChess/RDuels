package ru.merkii.rduels.core.arena.config;

import lombok.Getter;
import org.bukkit.Material;
import ru.merkii.rduels.config.settings.Config;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.model.EntityPosition;

import java.util.List;

@Getter
public class ArenaSettings extends Config {

    private List<ArenaModel> arenas = ArenaSettings.fastList(ArenaModel.builder().arenaName("default").displayName("Поле").onePosition(new EntityPosition("world", 50.0, 64.0, 50.0)).twoPosition(new EntityPosition("world", 50.0, 64.0, 52.0)).breaking(false).schematic("NO_SCHEMATIC").material(Material.GRASS).build());


}

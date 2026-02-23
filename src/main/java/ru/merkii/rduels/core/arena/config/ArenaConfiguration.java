package ru.merkii.rduels.core.arena.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.core.arena.model.ArenaModel;

import java.util.List;
import java.util.Map;

@ConfigInterface
public interface ArenaConfiguration {

    Map<ArenaModel, ItemBuilder> arenas();

    void arenas(Map<ArenaModel, ItemBuilder> newValue);

}
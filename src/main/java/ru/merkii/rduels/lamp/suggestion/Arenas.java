package ru.merkii.rduels.lamp.suggestion;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.arena.config.ArenaConfiguration;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import java.util.Collection;

public class Arenas implements SuggestionProvider<BukkitCommandActor> {
    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> executionContext) {
        ArenaConfiguration arenaConfiguration = RDuels.beanScope().get(ArenaConfiguration.class);
        return arenaConfiguration.arenas()
                .keySet()
                .stream()
                .map(ArenaModel::getArenaName)
                .toList();
    }
}
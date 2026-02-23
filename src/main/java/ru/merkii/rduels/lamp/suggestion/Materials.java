package ru.merkii.rduels.lamp.suggestion;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Materials implements SuggestionProvider<BukkitCommandActor> {
    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> executionContext) {
        return Arrays.stream(Material.values())
                .map(Material::name)
                .toList();
    }
}

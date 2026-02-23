package ru.merkii.rduels.lamp.suggestion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import java.util.Collection;

public class AllPlayers implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> executionContext) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .toList();
    }
}

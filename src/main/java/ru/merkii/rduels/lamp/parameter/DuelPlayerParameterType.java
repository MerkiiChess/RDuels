package ru.merkii.rduels.lamp.parameter;

import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.lamp.exception.DuelPlayerException;

@Singleton
public class DuelPlayerParameterType implements ParameterType<BukkitCommandActor, DuelPlayer> {

    @Override
    public DuelPlayer parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> executionContext) {
        String name = input.readString();
        Player bukkitPlayer = Bukkit.getPlayerExact(name);
        if (bukkitPlayer == null) {
            throw new DuelPlayerException();
        }
        return BukkitAdapter.adapt(bukkitPlayer);
    }

    @Override
    public @NotNull SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return (context) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

}

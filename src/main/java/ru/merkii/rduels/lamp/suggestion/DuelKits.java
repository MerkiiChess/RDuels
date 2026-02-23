package ru.merkii.rduels.lamp.suggestion;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.model.KitModel;
import java.util.Collection;

public class DuelKits implements SuggestionProvider<BukkitCommandActor> {
    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> executionContext) {
        KitConfiguration kitConfiguration = RDuels.beanScope().get(KitConfiguration.class);
        return kitConfiguration.kits()
                .keySet()
                .stream()
                .map(KitModel::getDisplayName)
                .toList();
    }
}

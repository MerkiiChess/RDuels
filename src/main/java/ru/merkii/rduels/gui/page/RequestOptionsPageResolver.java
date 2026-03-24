package ru.merkii.rduels.gui.page;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.core.arena.config.ArenaConfiguration;
import ru.merkii.rduels.core.duel.config.ChoiceKitMenuConfiguration;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;
import ru.merkii.rduels.model.DuelOptionModel;

import java.util.List;
import java.util.stream.Collectors;

public record RequestOptionsPageResolver(ChoiceKitMenuConfiguration config) implements PageResolver<DuelOptionModel> {

    @Override
    public List<DuelOptionModel> resolve(Player player, InventoryContext context) {
        String optionType = context.require("option_type");
        boolean ffa = (boolean) context.get("ffa").orElse(false);

        return switch (optionType) {
            case "num_games" -> config.requestNumGames().countFightNum().entrySet().stream()
                    .map(entry -> new DuelOptionModel(String.valueOf(entry.getKey()), "num_games", entry.getValue(), entry.getKey()))
                    .collect(Collectors.toList());
            case "kit" -> {
                KitConfiguration kitConfiguration = RDuels.beanScope().get(KitConfiguration.class);
                yield kitConfiguration.kits().entrySet().stream()
                        .map(entry -> new DuelOptionModel(entry.getKey().getDisplayName(), "kit", entry.getValue(), entry.getKey()))
                        .toList();
            }
            case "arena" -> {
                ArenaConfiguration arenaConfiguration = RDuels.beanScope().get(ArenaConfiguration.class);
                yield arenaConfiguration.arenas().entrySet().stream()
                        .filter(entry -> ffa == entry.getKey().isFfa())
                        .map(entry -> new DuelOptionModel(entry.getKey().getDisplayName(), "arena", entry.getValue(), entry.getKey()))
                        .collect(Collectors.toList());
            }
            default -> List.of();
        };
    }
}

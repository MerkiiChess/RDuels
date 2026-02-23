package ru.merkii.rduels.gui.page;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.core.arena.config.ArenaConfiguration;
import ru.merkii.rduels.core.duel.config.ChoiceKitMenuConfiguration;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;
import ru.merkii.rduels.model.DuelOptionModel;
import ru.merkii.rduels.model.KitModel;

import java.util.List;
import java.util.stream.Collectors;

public class RequestOptionsPageResolver implements PageResolver<DuelOptionModel> {

    private final ChoiceKitMenuConfiguration config;

    public RequestOptionsPageResolver(ChoiceKitMenuConfiguration config) {
        this.config = config;
    }

    @Override
    public List<DuelOptionModel> resolve(Player player, InventoryContext context) {
        String optionType = context.require("option_type");
        boolean ffa = (boolean) context.get("ffa").orElse(false);
        return switch (optionType) {
            case "num_games" -> config.requestNumGames().countFightNum().entrySet().stream()
                    .map(entry -> {
                        DuelOptionModel duelOptionModel = new DuelOptionModel(String.valueOf(entry.getKey()), "num_games", entry.getValue(), entry.getKey());
                        context.overrideOrCreate("option_model", duelOptionModel);
                        return duelOptionModel;
                    })
                    .collect(Collectors.toList());
            case "kit" -> {
                KitConfiguration kitConfiguration = RDuels.beanScope().get(KitConfiguration.class);
                yield kitConfiguration.kits()
                        .entrySet()
                        .stream()
                        .map(entry -> {
                            KitModel kitModel = entry.getKey();
                            DuelOptionModel duelOptionModel = new DuelOptionModel(kitModel.getDisplayName(), "kit", entry.getValue(), kitModel);
                            context.overrideOrCreate("option_model", duelOptionModel);
                            return duelOptionModel;
                        })
                        .toList();
            }
            case "arena" -> {
                ArenaConfiguration arenaConfiguration = RDuels.beanScope().get(ArenaConfiguration.class);
                yield arenaConfiguration.arenas().entrySet().stream()
                        .filter(entry -> ffa == entry.getKey().isFfa())
                        .map(entry -> {
                            DuelOptionModel duelOptionModel = new DuelOptionModel(entry.getKey().getDisplayName(), "arena", entry.getValue(), entry.getKey());
                            context.overrideOrCreate("option_model", duelOptionModel);
                            return duelOptionModel;
                        })
                        .collect(Collectors.toList());
            }
            default -> List.of();
        };
    }
}

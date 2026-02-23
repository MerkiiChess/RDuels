package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.menu.DuelChoiceKitMenu;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.model.DuelOptionModel;
import ru.merkii.rduels.model.KitModel;

public class SelectOptionClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "SELECT_OPTION";
    private final MenuConfiguration config;

    public SelectOptionClickHandler(MenuConfiguration config) {
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        DuelRequest duelRequest = context.require("duel_request");
        String optionType = context.require("option_type");
        DuelOptionModel duelOptionModel = context.require("option_model");
        Object model = duelOptionModel.model();
        String optionName = "";
        switch (optionType) {
            case "num_games":
                int num = (Integer) model;
                duelRequest.setNumGames(num);
                optionName = String.valueOf(num);
                break;
            case "kit":
                KitModel kitModel = (KitModel) model;
                duelRequest.setKitModel(kitModel);
                optionName = kitModel.getDisplayName();
                break;
            case "arena":
                ArenaModel arenaModel = (ArenaModel) model;
                duelRequest.setArena(arenaModel);
                optionName = arenaModel.getDisplayName();
                break;
        }
        config.settings().notification().playSound(player, "select-option");
        config.messages().sendTo(player, Placeholder.wrapped("%option_name%", optionName), "option-selected");
        new DuelChoiceKitMenu().open(player, duelRequest, (Boolean) context.get("ffa").orElse(false));

    }
}

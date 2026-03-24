package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.matchmaking.DuelMatchmakingService;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.model.DuelOptionModel;
import ru.merkii.rduels.model.KitModel;

import java.util.Optional;

public record MatchmakingSelectKitClickHandler(DuelMatchmakingService matchmakingService, ArenaAPI arenaAPI,
                                               DuelAPI duelAPI) implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "MATCHMAKING_SELECT_KIT";

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        DuelOptionModel option = context.require("model");
        KitModel kitModel = (KitModel) option.model();
        DuelPlayer opponent = matchmakingService.getOpponentAndRemove(kitModel);

        if (opponent == null || !opponent.isOnline() || duelAPI.isFightPlayer(opponent)) {
            matchmakingService.addInQueue(duelPlayer, kitModel);
            player.closeInventory();
            return;
        }

        DuelRequest request = DuelRequest.create(opponent, duelPlayer);
        request.setKitModel(kitModel);
        request.setNumGames(1);
        ArenaModel arena = getArena(kitModel);
        if (arena == null) {
            matchmakingService.addInQueue(opponent, kitModel);
            matchmakingService.addInQueue(duelPlayer, kitModel);
            player.closeInventory();
            return;
        }
        request.setArena(arena);
        duelAPI.startFight(request);
        player.closeInventory();
    }

    private ArenaModel getArena(KitModel kitModel) {
        if (!kitModel.isBindingArena()) {
            return arenaAPI.getFreeArena().get();
        }
        Optional<ArenaModel> optionalArena = arenaAPI.getArenaFromKit(kitModel);
        return optionalArena.orElse(null);
    }

}

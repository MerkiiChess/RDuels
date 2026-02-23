package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.model.KitModel;

import java.util.Optional;

public class RequestFightClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "REQUEST_FIGHT";
    private final CustomKitAPI customKitAPI;
    private final DuelAPI duelAPI;
    private final ArenaAPI arenaAPI;

    public RequestFightClickHandler(CustomKitAPI customKitAPI, DuelAPI duelAPI, ArenaAPI arenaAPI) {
        this.customKitAPI = customKitAPI;
        this.duelAPI = duelAPI;
        this.arenaAPI = arenaAPI;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        DuelRequest duelRequest = context.require("duel_request");
        if (duelRequest.getKitModel() == null) {
            DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
            KitModel kitModel = getKitModel(duelRequest, duelPlayer);
            duelRequest.setKitModel(kitModel);
        }
        if (duelRequest.getNumGames() == 0) {
            duelRequest.setNumGames(1);
        }
        if (duelRequest.getArena() == null) {
            ArenaModel arenaModel = getArena(duelRequest.getKitModel());
            if (arenaModel == null) {
                player.closeInventory();
                return;
            }
            duelRequest.setArena(arenaModel);
        }
        duelAPI.addRequest(duelRequest);
        player.closeInventory();
    }

    private ArenaModel getArena(KitModel kitModel) {
        if (!kitModel.isBindingArena()) {
            return arenaAPI.getFreeArena();
        }
        Optional<ArenaModel> optionalArena = arenaAPI.getArenaFromKit(kitModel);
        return optionalArena.orElse(null);
    }

    private KitModel getKitModel(DuelRequest duelRequest, DuelPlayer player) {
        return duelRequest.getDuelKit() == DuelKitType.CUSTOM
                ? customKitAPI.getKitModel(player)
                : duelAPI.getRandomKit();
    }
}

package ru.merkii.rduels.core.duel;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.fight.DuelFightService;
import ru.merkii.rduels.core.duel.kit.KitService;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.duel.movement.DuelMovementService;
import ru.merkii.rduels.core.duel.preparation.PlayerPreparationService;
import ru.merkii.rduels.core.duel.request.DuelRequestService;
import ru.merkii.rduels.core.duel.schedualer.DuelTeleportScheduler;
import ru.merkii.rduels.core.duel.spectator.DuelSpectatorService;
import ru.merkii.rduels.core.duel.teleport.DuelTeleportService;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.KitModel;

import java.util.List;
import java.util.Optional;

@Singleton
public class DuelService implements DuelAPI {

    private final KitService kitService;
    private final DuelRequestService requestService;
    private final DuelFightService fightService;
    private final DuelSpectatorService spectatorService;
    private final DuelMovementService movementService;
    private final PlayerPreparationService preparationService;
    private final DuelTeleportService teleportService;

    @Inject
    public DuelService(
            KitService kitService,
            DuelRequestService requestService,
            DuelFightService fightService,
            DuelSpectatorService spectatorService,
            DuelMovementService movementService,
            PlayerPreparationService preparationService,
            DuelTeleportService teleportService) {
        this.kitService = kitService;
        this.requestService = requestService;
        this.fightService = fightService;
        this.spectatorService = spectatorService;
        this.movementService = movementService;
        this.preparationService = preparationService;
        this.teleportService = teleportService;
    }

    @Override
    public KitModel getKitFromName(String kitName) {
        return kitService.getKitFromName(kitName);
    }

    @Override
    public boolean isFightPlayer(DuelPlayer player) {
        return fightService.isFightPlayer(player);
    }

    @Override
    public DuelFightModel getFightModelFromPlayer(DuelPlayer player) {
        return fightService.getFightModelFromPlayer(player);
    }

    @Override
    public void addRequest(DuelRequest duelRequest) {
        requestService.addRequest(duelRequest);
    }

    @Override
    public void removeRequest(DuelRequest duelRequest) {
        requestService.removeRequest(duelRequest);
    }

    @Override
    public void startFight(DuelRequest duelRequest) {
        fightService.startFight(duelRequest);
    }

    @Override
    public void startFightFour(DuelPlayer p1, DuelPlayer p2, DuelPlayer p3, DuelPlayer p4, DuelRequest r) {
        fightService.startFightFour(p1, p2, p3, p4, r);
    }

    @Override
    public void nextRound(DuelFightModel f) {
        fightService.nextRound(f);
    }

    @Override
    public void stopFight(DuelFightModel f, DuelPlayer w, DuelPlayer l) {
        fightService.stopFight(f, w, l);
    }

    @Override
    public List<DuelRequest> getRequestsFromReceiver(DuelPlayer r) {
        return requestService.getRequestsFromReceiver(r);
    }

    @Override
    public DuelPlayer getWinnerFromFight(DuelFightModel f, DuelPlayer l) {
        return fightService.getWinnerFromFight(f, l);
    }

    @Override
    public DuelPlayer getLoserFromFight(DuelFightModel f, DuelPlayer w) {
        return fightService.getLoserFromFight(f, w);
    }

    @Override
    public DuelPlayer getOpponentFromFight(DuelFightModel f, DuelPlayer p) {
        return fightService.getOpponentFromFight(f, p);
    }

    @Override
    public DuelPlayer getOpponentFromFight(DuelPlayer p) {
        return fightService.getOpponentFromFight(p);
    }

    @Override
    public DuelRequest getRequestFromSender(DuelPlayer s, DuelPlayer r) {
        return requestService.getRequestFromSender(s, r);
    }

    @Override
    public void saveKitServer(DuelPlayer p, String name) {
        kitService.saveKitServer(BukkitAdapter.adapt(p), name);
    }

    @Override
    public boolean isKitNameContains(String name) {
        return kitService.isKitNameContains(name);
    }

    @Override
    public int getFreeSlotKit() {
        return kitService.getFreeSlotKit();
    }

    @Override
    public EntityPosition getRandomSpawn() {
        return preparationService.getRandomSpawn();
    }

    @Override
    public void giveStartItems(DuelPlayer p) {
        preparationService.giveStartItems(p);
    }

    @Override
    public KitModel getRandomKit() {
        return kitService.getRandomKit();
    }

    @Override
    public void addNoMove(DuelPlayer p) {
        movementService.addNoMove(p);
    }

    @Override
    public void removeNoMove(DuelPlayer p) {
        movementService.removeNoMove(p);
    }

    @Override
    public boolean isNoMovePlayer(DuelPlayer p) {
        return movementService.isNoMovePlayer(p);
    }

    @Override
    public void addSpectate(DuelPlayer p, DuelFightModel f) {
        spectatorService.addSpectate(p, f);
    }

    @Override
    public void removeSpectate(DuelPlayer p, DuelFightModel f, boolean fighting) {
        spectatorService.removeSpectate(p, f, fighting);
    }

    @Override
    public boolean isSpectate(DuelPlayer p) {
        return spectatorService.isSpectate(p);
    }

    @Override
    public DuelFightModel getDuelFightModelFromSpectator(DuelPlayer p) {
        return spectatorService.getDuelFightModelFromSpectator(p);
    }

    @Override
    public void preparationToFight(List<DuelPlayer> players) {
        preparationService.preparationToFight(players);
    }

    @Override
    public void preparationToFight(DuelPlayer... players) {
        preparationService.preparationToFight(players);
    }

    @Override
    public void preparationToFight(PartyModel s, PartyModel r) {
        preparationService.preparationToFight(s, r);
    }

    @Override
    public Optional<DuelTeleportScheduler> getTeleportSchedulerFromFight(DuelFightModel f) {
        return teleportService.getTeleportSchedulerFromFight(f);
    }
}

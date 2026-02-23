package ru.merkii.rduels.core.duel.fight;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.bucket.DuelFightBucket;
import ru.merkii.rduels.core.duel.bucket.DuelTeleportBucket;
import ru.merkii.rduels.core.duel.config.DuelConfiguration;
import ru.merkii.rduels.core.duel.config.LoseConfiguration;
import ru.merkii.rduels.core.duel.config.TitleSettingsConfiguration;
import ru.merkii.rduels.core.duel.config.WinConfiguration;
import ru.merkii.rduels.core.duel.model.*;
import ru.merkii.rduels.core.duel.movement.DuelMovementService;
import ru.merkii.rduels.core.duel.preparation.PlayerPreparationService;
import ru.merkii.rduels.core.duel.schedualer.DuelScheduler;
import ru.merkii.rduels.core.duel.schedualer.DuelTeleportScheduler;
import ru.merkii.rduels.core.duel.spectator.DuelSpectatorService;
import ru.merkii.rduels.core.duel.teleport.DuelTeleportService;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.core.duel.event.DuelStartFightEvent;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.util.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class DuelFightServiceImpl implements DuelFightService {

    private final DuelFightBucket fightBucket;
    private final ArenaAPI arenaAPI;
    private final PartyAPI partyAPI;
    private final SignAPI signAPI;
    private final PlayerPreparationService preparationService;
    private final DuelTeleportService teleportService;
    private final DuelSpectatorService spectatorService;
    private final DuelMovementService movementService;
    private final DuelTeleportBucket duelTeleportBucket;
    private final MessageConfig config;
    private final SettingsConfiguration settings;
    private final TitleSettingsConfiguration titleSettingsConfiguration;

    @Inject
    public DuelFightServiceImpl(
            DuelFightBucket fightBucket,
            ArenaAPI arenaAPI,
            PartyAPI partyAPI,
            SignAPI signAPI,
            PlayerPreparationService preparationService,
            DuelTeleportService teleportService,
            DuelSpectatorService spectatorService,
            DuelMovementService movementService,
            DuelTeleportBucket duelTeleportBucket,
            MessageConfig config,
            SettingsConfiguration settings,
            DuelConfiguration duelConfiguration) {
        this.fightBucket = fightBucket;
        this.arenaAPI = arenaAPI;
        this.partyAPI = partyAPI;
        this.signAPI = signAPI;
        this.preparationService = preparationService;
        this.teleportService = teleportService;
        this.spectatorService = spectatorService;
        this.movementService = movementService;
        this.duelTeleportBucket = duelTeleportBucket;
        this.config = config;
        this.settings = settings;
        this.titleSettingsConfiguration = duelConfiguration.titleSettings();
    }

    @Override
    public boolean isFightPlayer(DuelPlayer player) {
        return fightBucket.getDuelFights().stream()
                .anyMatch(fightModel -> isPlayerInFight(fightModel, player));
    }

    @Override
    public DuelFightModel getFightModelFromPlayer(DuelPlayer player) {
        return fightBucket.getDuelFights().stream()
                .filter(fightModel -> isPlayerInFight(fightModel, player))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void startFight(DuelRequest duelRequest) {
        DuelPlayer sender = getOwnerOrPlayer(duelRequest.getSenderParty(), duelRequest.getSender());
        DuelPlayer receiver = getOwnerOrPlayer(duelRequest.getReceiverParty(), duelRequest.getReceiver());

        if (sender == null || receiver == null) return;

        boolean isFfa = duelRequest.getReceiverParty() != null && duelRequest.getSenderParty() != null;
        ArenaModel arenaModel = selectValidArena(duelRequest.getArena(), isFfa);
        if (arenaModel == null) {
            sendTo(Arrays.asList(sender, receiver), config.message("duel-arenas-full"));
            removeSignIfPresent(duelRequest);
            return;
        }

        arenaAPI.restoreArena(arenaModel);

        DuelFightModel duelFightModel = new DuelFightModel(sender, receiver, duelRequest.getNumGames(), duelRequest.getKitModel(), arenaModel);
        if (duelRequest.getSignModel() != null) {
            duelFightModel.setSignModel(duelRequest.getSignModel());
        }

        List<DuelPlayer> allPlayers = getAllPlayersFromRequest(duelRequest);
        preparationService.preparationToFight(allPlayers);

        this.fightBucket.addFight(duelFightModel);
        arenaAPI.addBusyArena(arenaModel);

        if (isFfa) {
            handlePartyFightStart(duelRequest, duelFightModel);
        } else {
            teleportPlayersToArena(duelRequest, duelFightModel);
            duelFightModel.getKitModel().giveItemPlayers(allPlayers.stream().map(BukkitAdapter::adapt).toList().toArray(new Player[0]));
            DuelStartFightEvent.create(sender, receiver, duelFightModel).call();
        }

        signAPI.removePlayerQueueSign(allPlayers.toArray(new DuelPlayer[0]));
        duelFightModel.setBukkitTask(new DuelScheduler(TimeUtil.parseTime(this.settings.durationFight(), TimeUnit.MINUTES), duelFightModel));

        this.duelTeleportBucket.add(new DuelTeleportScheduler(duelFightModel));

        removeNonPlayerEntitiesNear(sender);
    }

    @Override
    public void startFightFour(DuelPlayer player1, DuelPlayer player2, DuelPlayer player3, DuelPlayer player4, DuelRequest duelRequest) {
        ArenaModel arenaModel = selectValidArena(duelRequest.getArena(), true);
        if (arenaModel == null) {
            sendTo(Arrays.asList(player1, player2, player3, player4), config.message("duel-arenas-full"));
            removeSignIfPresent(duelRequest);
            return;
        }

        DuelFightModel duelFightModel = new DuelFightModel(player1, player3, duelRequest.getNumGames(), duelRequest.getKitModel(), arenaModel);
        if (duelRequest.getSignModel() != null) {
            duelFightModel.setSignModel(duelRequest.getSignModel());
        }
        duelFightModel.setPlayer2(player2);
        duelFightModel.setPlayer4(player4);

        List<DuelPlayer> allPlayers = Arrays.asList(player1, player2, player3, player4);
        preparationService.preparationToFight(allPlayers);
        signAPI.removePlayerQueueSign(allPlayers.toArray(new DuelPlayer[0]));

        Map<Integer, EntityPosition> positions = arenaModel.getFfaPositions();
        player1.teleport(positions.get(1));
        player2.teleport(positions.get(2));
        player3.teleport(positions.get(11));
        player4.teleport(positions.get(12));

        duelFightModel.getKitModel().giveItemPlayers(allPlayers.stream().map(BukkitAdapter::adapt).toList().toArray(new Player[0]));

        duelFightModel.setBukkitTask(new DuelScheduler(TimeUtil.parseTime(this.settings.durationFight(), TimeUnit.MINUTES), duelFightModel));
        this.fightBucket.addFight(duelFightModel);
        this.duelTeleportBucket.add(new DuelTeleportScheduler(duelFightModel));
        removeNonPlayerEntitiesNear(player1);
    }

    @Override
    public void nextRound(DuelFightModel duelFight) {
        if (!this.fightBucket.getDuelFights().contains(duelFight)) return;

        ArenaModel arenaModel = duelFight.getArenaModel();
        List<DuelPlayer> allPlayers = getAllPlayersFromFight(duelFight);

        preparationService.preparationToFight(allPlayers);
        teleportPlayersForNextRound(duelFight);

        duelFight.getKitModel().giveItemPlayers(allPlayers.stream().map(BukkitAdapter::adapt).toList().toArray(new Player[0]));

        duelFight.getSpectates().stream()
                .map(BukkitAdapter::getPlayer)
                .forEach(spectator -> spectator.teleport(arenaModel.getSpectatorPosition()));

        removeNonPlayerEntitiesNear(duelFight.getSender());

        if (arenaModel.isBreaking()) {
            arenaAPI.restoreArena(arenaModel);
        }

        duelFight.getBukkitTask().updateTime(duelFight);
        this.duelTeleportBucket.add(new DuelTeleportScheduler(duelFight));
    }

    @Override
    public void stopFight(DuelFightModel duelFightModel, DuelPlayer winner, DuelPlayer loser) {
        getTeleportSchedulerFromFight(duelFightModel).ifPresent(scheduler -> {
            scheduler.cancel();
            this.duelTeleportBucket.remove(scheduler);
        });

        removeNoMoveFromAllPlayers(duelFightModel);

        this.fightBucket.removeFight(duelFightModel);
        arenaAPI.removeBusyArena(duelFightModel.getArenaModel());

        if (duelFightModel.getArenaModel().isFfa() && duelFightModel.getSenderParty() != null && duelFightModel.getReceiverParty() != null) {
            partyAPI.removeFightParty(duelFightModel.getSenderParty(), duelFightModel.getReceiverParty());
        }

        List<DuelPlayer> winnerTeam = getTeamPlayers(duelFightModel, winner);
        List<DuelPlayer> loserTeam = getTeamPlayers(duelFightModel, loser);

        WinConfiguration win = titleSettingsConfiguration.win();
        LoseConfiguration lose = titleSettingsConfiguration.lose();

        sendTitles(winnerTeam.stream().map(BukkitAdapter::adapt).toList(), win.text(), win.fadeIn(), win.stay(), win.fadeOut());
        sendTitles(loserTeam.stream().map(BukkitAdapter::adapt).toList(), lose.text(), lose.fadeIn(), lose.stay(), lose.fadeOut());

        String winnersStr = winnerTeam.stream().map(DuelPlayer::getName).collect(Collectors.joining(" "));
        String losersStr = loserTeam.stream().map(DuelPlayer::getName).collect(Collectors.joining(" "));

        Placeholder.Placeholders placeholder = Placeholder.Placeholders.of(
                Placeholder.of("(playersHealth)", getHealthPlayers(duelFightModel, loser)),
                Placeholder.of("(winner)", winner == null ? config.plainMessage("replace-winner-null") : winnersStr),
                Placeholder.of("(loser)", loser == null ? config.plainMessage("replace-loser-null") : losersStr),
                Placeholder.of("(kit)", duelFightModel.getKitModel().getDisplayName()),
                Placeholder.of("(numGames)", String.valueOf(duelFightModel.getNumGames()))
        );

        Component message = config.message(placeholder, "end-fight");
        List<DuelPlayer> allPlayers = new ArrayList<>(winnerTeam);
        allPlayers.addAll(loserTeam);
        sendTo(allPlayers, message);

        EntityPosition spawn = preparationService.getRandomSpawn();
        resetAndTeleportPlayers(winnerTeam, spawn, win.text(), win.fadeIn(), win.stay(), win.fadeOut());
        resetAndTeleportPlayers(loserTeam, spawn, lose.text(), lose.fadeIn(), lose.stay(), lose.fadeOut());

        if (duelFightModel.getSignModel() != null) {
            signAPI.removeSignFight(duelFightModel.getSignModel());
        }

        duelFightModel.getBukkitTask().cancel();

        removeAllSpectators(duelFightModel);

        if (duelFightModel.getArenaModel().isBreaking()) {
            arenaAPI.restoreArena(duelFightModel.getArenaModel());
        }

        DuelStopFightEvent.create(duelFightModel.getSender(), duelFightModel.getReceiver(), winner, loser, duelFightModel).call();
    }

    @Override
    public DuelPlayer getWinnerFromFight(DuelFightModel duelFightModel, DuelPlayer loser) {
        return duelFightModel.getReceiver().getUUID().equals(loser.getUUID())
                ? duelFightModel.getSender() : duelFightModel.getReceiver();
    }

    @Override
    public DuelPlayer getLoserFromFight(DuelFightModel duelFightModel, DuelPlayer winner) {
        return duelFightModel.getReceiver().getUUID().equals(winner.getUUID())
                ? duelFightModel.getSender() : duelFightModel.getReceiver();
    }

    @Override
    public DuelPlayer getOpponentFromFight(DuelFightModel duelFightModel, DuelPlayer player) {
        return duelFightModel.getReceiver().getUUID().equals(player.getUUID())
                ? duelFightModel.getSender() : duelFightModel.getReceiver();
    }

    @Override
    public DuelPlayer getOpponentFromFight(DuelPlayer player) {
        DuelFightModel fightModel = this.getFightModelFromPlayer(player);
        return fightModel != null ? getOpponentFromFight(fightModel, player) : null;
    }

    private boolean isPlayerInFight(DuelFightModel fightModel, DuelPlayer player) {
        UUID playerUUID = player.getUUID();
        if (fightModel.getArenaModel().isFfa()) {
            if (fightModel.getReceiverParty() != null && fightModel.getSenderParty() != null) {
                return fightModel.getReceiverParty().getPlayers().contains(playerUUID) ||
                        fightModel.getSenderParty().getPlayers().contains(playerUUID);
            } else {
                return (fightModel.getPlayer2() != null && fightModel.getPlayer2().getUUID().equals(playerUUID)) ||
                        (fightModel.getPlayer4() != null && fightModel.getPlayer4().getUUID().equals(playerUUID));
            }
        }
        return fightModel.getReceiver().getUUID().equals(playerUUID) ||
                fightModel.getSender().getUUID().equals(playerUUID);
    }

    private ArenaModel selectValidArena(ArenaModel initialArena, boolean isFfa) {
        ArenaModel arenaModel = initialArena;
        if (arenaModel == null || arenaAPI.isBusyArena(arenaModel)) {
            arenaModel = isFfa ? arenaAPI.getFreeArenaFFA() : arenaAPI.getFreeArenaName(initialArena.getDisplayName());
            if (arenaModel == null) return null;
        }

        if (hasLocationErrors(arenaModel)) {
            ArenaModel oldArena = arenaModel.clone();
            int attempts = 5;
            while (attempts > 0) {
                arenaModel = isFfa ? arenaAPI.getFreeArenaFFA() : arenaAPI.getFreeArena();
                if (arenaModel != null && !arenaModel.equals(oldArena) && !hasLocationErrors(arenaModel)) {
                    break;
                }
                attempts--;
            }
            if (attempts <= 0) return null;
        }
        return arenaModel;
    }

    private boolean hasLocationErrors(ArenaModel arenaModel) {
        if (arenaModel.isFfa()) {
            return arenaModel.getFfaPositions().values().stream().anyMatch(pos -> pos.getWorldName() == null);
        } else {
            return arenaModel.getOnePosition() == null || arenaModel.getOnePosition().getWorldName() == null ||
                    arenaModel.getTwoPosition() == null || arenaModel.getTwoPosition().getWorldName() == null;
        }
    }

    private List<DuelPlayer> getAllPlayersFromRequest(DuelRequest duelRequest) {
        List<DuelPlayer> players = new ArrayList<>();
        if (duelRequest.getSenderParty() != null) {
            players.addAll(PlayerUtil.duelPlayersConvertListUUID(duelRequest.getSenderParty().getPlayers()));
        } else {
            players.add(duelRequest.getSender());
        }
        if (duelRequest.getReceiverParty() != null) {
            players.addAll(PlayerUtil.duelPlayersConvertListUUID(duelRequest.getReceiverParty().getPlayers()));
        } else {
            players.add(duelRequest.getReceiver());
        }
        return players;
    }

    private void handlePartyFightStart(DuelRequest duelRequest, DuelFightModel duelFightModel) {
        PartyModel receiverParty = duelRequest.getReceiverParty();
        PartyModel senderParty = duelRequest.getSenderParty();
        duelFightModel.setReceiverParty(receiverParty);
        duelFightModel.setSenderParty(senderParty);

        partyAPI.teleportToArena(duelRequest);

        List<DuelPlayer> senderPlayers = PlayerUtil.duelPlayersConvertListUUID(senderParty.getPlayers());
        List<DuelPlayer> receiverPlayers = PlayerUtil.duelPlayersConvertListUUID(receiverParty.getPlayers());

        preparationService.preparationToFight(senderPlayers);
        preparationService.preparationToFight(receiverPlayers);

        duelFightModel.getKitModel().giveItemPlayers(senderPlayers.stream().map(BukkitAdapter::adapt).toList().toArray(new Player[0]));
        duelFightModel.getKitModel().giveItemPlayers(receiverPlayers.stream().map(BukkitAdapter::adapt).toList().toArray(new Player[0]));

        partyAPI.addFightParty(senderParty, receiverParty);
        signAPI.removePlayerQueueSign(senderPlayers.toArray(new DuelPlayer[0]));
        signAPI.removePlayerQueueSign(receiverPlayers.toArray(new DuelPlayer[0]));

        DuelStartFightEvent.create(senderParty, receiverParty, duelFightModel).call();
    }

    private void teleportPlayersToArena(DuelRequest duelRequest, DuelFightModel duelFightModel) {
        ArenaModel arena = duelRequest.getArena();
        if (arena.isFfa()) {
            Map<Integer, EntityPosition> pos = arena.getFfaPositions();
            duelRequest.getSender().teleport(pos.get(1));
            duelRequest.getReceiver().teleport(pos.get(11));
        } else {
            duelRequest.getSender().teleport(arena.getOnePosition());
            duelRequest.getReceiver().teleport(arena.getTwoPosition());
        }
    }

    private void removeSignIfPresent(DuelRequest duelRequest) {
        if (duelRequest.getSignModel() != null) {
            signAPI.removeSignFight(duelRequest.getSignModel());
        }
    }

    private void removeNonPlayerEntitiesNear(DuelPlayer player) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        bukkitPlayer.getNearbyEntities(100.0, 100.0, 100.0).stream()
                .filter(entity -> !(entity instanceof Player))
                .forEach(Entity::remove);
    }

    private List<DuelPlayer> getAllPlayersFromFight(DuelFightModel duelFight) {
        List<DuelPlayer> players = new ArrayList<>();
        players.add(duelFight.getSender());
        players.add(duelFight.getReceiver());
        if (duelFight.getPlayer2() != null) players.add(duelFight.getPlayer2());
        if (duelFight.getPlayer4() != null) players.add(duelFight.getPlayer4());
        if (duelFight.getSenderParty() != null) {
            players.addAll(PlayerUtil.duelPlayersConvertListUUID(duelFight.getSenderParty().getPlayers()));
        }
        if (duelFight.getReceiverParty() != null) {
            players.addAll(PlayerUtil.duelPlayersConvertListUUID(duelFight.getReceiverParty().getPlayers()));
        }
        return players;
    }

    private void teleportPlayersForNextRound(DuelFightModel duelFight) {
        ArenaModel arenaModel = duelFight.getArenaModel();
        if (arenaModel.isFfa()) {
            if (duelFight.getReceiverParty() != null && duelFight.getSenderParty() != null) {
                partyAPI.teleportToArena(duelFight);
            } else if (duelFight.getPlayer2() != null && duelFight.getPlayer4() != null) {
                Map<Integer, EntityPosition> pos = arenaModel.getFfaPositions();
                duelFight.getSender().teleport(pos.get(1));
                duelFight.getPlayer2().teleport(pos.get(2));
                duelFight.getReceiver().teleport(pos.get(11));
                duelFight.getPlayer4().teleport(pos.get(12));
            }
        } else {
            duelFight.getSender().teleport(arenaModel.getOnePosition());
            duelFight.getReceiver().teleport(arenaModel.getTwoPosition());
        }
    }

    private void removeNoMoveFromAllPlayers(DuelFightModel duelFightModel) {
        movementService.removeNoMove(duelFightModel.getSender());
        movementService.removeNoMove(duelFightModel.getReceiver());
        if (duelFightModel.getArenaModel().isFfa()) {
            if (duelFightModel.getSenderParty() != null && duelFightModel.getReceiverParty() != null) {
                duelFightModel.getSenderParty().getPlayers()
                        .stream()
                        .map(BukkitAdapter::getPlayer)
                        .forEach(movementService::removeNoMove);
                duelFightModel.getReceiverParty().getPlayers()
                        .stream()
                        .map(BukkitAdapter::getPlayer)
                        .forEach(movementService::removeNoMove);
            } else {
                movementService.removeNoMove(duelFightModel.getPlayer2());
                movementService.removeNoMove(duelFightModel.getPlayer4());
            }
        }
    }

    private List<DuelPlayer> getTeamPlayers(DuelFightModel duelFightModel, DuelPlayer referencePlayer) {
        if (referencePlayer == null) return Collections.emptyList();

        List<DuelPlayer> team = new ArrayList<>();
        PartyModel party = partyAPI.getPartyModelFromPlayer(referencePlayer);

        if (party != null) {
            team.addAll(PlayerUtil.duelPlayersConvertListUUID(party.getPlayers()));
            team.add(BukkitAdapter.getPlayer(party.getOwner()));
        } else if (duelFightModel.getSender().equals(referencePlayer) || (duelFightModel.getPlayer2() != null && duelFightModel.getPlayer2().equals(referencePlayer))) {
            team.add(duelFightModel.getSender());
            if (duelFightModel.getPlayer2() != null) team.add(duelFightModel.getPlayer2());
        } else if (duelFightModel.getReceiver().equals(referencePlayer) || (duelFightModel.getPlayer4() != null && duelFightModel.getPlayer4().equals(referencePlayer))) {
            team.add(duelFightModel.getReceiver());
            if (duelFightModel.getPlayer4() != null) team.add(duelFightModel.getPlayer4());
        } else {
            team.add(referencePlayer);
        }

        return team.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void sendTitles(List<Player> players, String text, int fadeIn, int stay, int fadeOut) {
        text = ColorUtil.color(text);
        String finalText = text;
        players.forEach(player -> player.sendTitle(finalText, "", fadeIn, stay, fadeOut));
    }

    private void resetAndTeleportPlayers(List<DuelPlayer> players, EntityPosition position, String text, int fadeIn, int stay, int fadeOut) {
        players.forEach(player -> {
            player.teleport(position);
            Player bukkitPlayer = BukkitAdapter.adapt(player);
            PlayerUtil.clearEffects(bukkitPlayer);
            PlayerUtil.healPlayers(bukkitPlayer);
            player.setGameMode(GameMode.SURVIVAL);
            sendTitles(Collections.singletonList(bukkitPlayer), text, fadeIn, stay, fadeOut);

            if (player.isPartyExists()) {
                partyAPI.giveStartItems(player);
            } else {
                preparationService.giveStartItems(player);
            }
        });
    }

    private void removeAllSpectators(DuelFightModel duelFightModel) {
        try {
            duelFightModel.getSpectates().stream()
                    .map(BukkitAdapter::getPlayer)
                    .forEach(spectator -> spectatorService.removeSpectate(spectator, duelFightModel, false));
        } catch (ConcurrentModificationException ignored) {
            Iterator<UUID> iterator = duelFightModel.getSpectates().iterator();
            while (iterator.hasNext()) {
                DuelPlayer spectator = BukkitAdapter.getPlayer(iterator.next());
                spectatorService.removeSpectate(spectator, duelFightModel, false);
            }
        }
    }

    private String getHealthPlayers(DuelFightModel duelFightModel, DuelPlayer loser) {
        if (loser == null) return "";

        DuelPlayer winner = getWinnerFromFight(duelFightModel, loser);
        List<DuelPlayer> winnerTeam = getTeamPlayers(duelFightModel, winner);

        return winnerTeam.stream()
                .filter(player -> player.getGameMode() != GameMode.SPECTATOR)
                .map(BukkitAdapter::adapt)
                .map(player -> player.getName() + " " + player.getHealth())
                .collect(Collectors.joining(", "));
    }

    private Optional<DuelTeleportScheduler> getTeleportSchedulerFromFight(DuelFightModel duelFightModel) {
        return teleportService.getTeleportSchedulerFromFight(duelFightModel);
    }

    private DuelPlayer getOwnerOrPlayer(PartyModel party, DuelPlayer player) {
        return party != null ? BukkitAdapter.getPlayer(party.getOwner()) : player;
    }

    private void sendTo(List<DuelPlayer> players, Component messaage) {
        players.forEach(player -> player.sendMessage(messaage));
    }

}

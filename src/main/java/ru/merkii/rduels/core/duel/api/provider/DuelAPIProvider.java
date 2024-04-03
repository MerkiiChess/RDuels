package ru.merkii.rduels.core.duel.api.provider;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.config.settings.*;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.bucket.*;
import ru.merkii.rduels.core.duel.event.*;
import ru.merkii.rduels.core.duel.model.*;
import ru.merkii.rduels.core.duel.schedualer.*;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.sign.SignCore;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.KitModel;
import ru.merkii.rduels.util.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DuelAPIProvider implements DuelAPI {

    private final RDuels plugin = RDuels.getInstance();
    private final MessageConfiguration messageConfiguration = plugin.getPluginMessage();
    private final Settings settings = plugin.getSettings();
    private final KitConfig kitConfig = plugin.getKitConfig();
    private final ArenaCore arenaCore = ArenaCore.INSTANCE;
    private final DuelCore duelCore = DuelCore.INSTANCE;
    private final DuelFightBucket fightBucket = duelCore.getDuelFightBucket();
    private final DuelRequestsBucket requestsBucket = duelCore.getDuelRequestsBucket();
    private final DuelMoveBucket duelMoveBucket = new DuelMoveBucket();
    private final DuelTeleportBucket duelTeleportBucket = new DuelTeleportBucket();

    /**
     * Retrieves the kit model based on the given kit name.
     *
     * @param kitName The name of the kit.
     * @return The KitModel corresponding to the given kit name, or null if not found.
     */
    @Override
    public KitModel getKitFromName(String kitName) {
        return this.kitConfig.getKits().stream().filter(model -> model.getDisplayName().equalsIgnoreCase(kitName)).findFirst().orElse(null);
    }

    /**
     * Checks if the player is involved in a fight.
     *
     * @param player The player to check.
     * @return True if the player is involved in a fight, false otherwise.
     */
    @Override
    public boolean isFightPlayer(Player player) {
        for (DuelFightModel fightModel : this.fightBucket.getDuelFights()) {
            if (fightModel.getArenaModel().isFfa()) {
                assert fightModel.getReceiverParty() != null;
                if (fightModel.getReceiverParty().getPlayers().contains(player.getUniqueId()) || Objects.requireNonNull(fightModel.getSenderParty()).getPlayers().contains(player.getUniqueId())) return true;
            }
            if (fightModel.getReceiver().equals(player) || fightModel.getSender().equals(player)) return true;
        }
        return false;
    }

    /**
     * Retrieves the DuelFightModel associated with the given player.
     *
     * @param player The player for whom to retrieve the DuelFightModel.
     * @return The DuelFightModel associated with the player, or null if not found.
     */
    @Override
    public @Nullable DuelFightModel getFightModelFromPlayer(Player player) {
        for (DuelFightModel fightModel : this.fightBucket.getDuelFights()) {
            if (fightModel.getArenaModel().isFfa()) {
                assert fightModel.getSenderParty() != null;
                if (fightModel.getSenderParty().getPlayers().contains(player.getUniqueId()) || Objects.requireNonNull(fightModel.getReceiverParty()).getPlayers().contains(player.getUniqueId())) return fightModel;
            }
            if (fightModel.getReceiver().getUniqueId().equals(player.getUniqueId()) || fightModel.getSender().getUniqueId().equals(player.getUniqueId())) return fightModel;
        }
        return null;
    }

    /**
     * Adds a duel request to the system.
     *
     * @param duelRequest The DuelRequest to add.
     */
    @Override
    public void addRequest(DuelRequest duelRequest) {
        this.requestsBucket.addRequest(duelRequest);
        Player sender = duelRequest.getSenderParty() != null ? Bukkit.getPlayer(duelRequest.getSenderParty().getOwner()) : duelRequest.getSender();
        Player receiver = duelRequest.getReceiverParty() != null ? Bukkit.getPlayer(duelRequest.getReceiverParty().getOwner()) : duelRequest.getReceiver();
        assert sender != null;
        assert receiver != null;
        sender.sendMessage(this.messageConfiguration.getMessage("requestSender").replace("(player)", duelRequest.getReceiver().getName()));
        // КНОПКИ
        Component accept = Component.text(this.messageConfiguration.getMessage("acceptButton")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/duel yes " + sender.getName()));
        Component decline = Component.text(this.messageConfiguration.getMessage("declineButton")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/duel no " + sender.getName()));
        this.messageConfiguration.getMessages("requestReceiver")
                .stream()
                .map(str -> str.replace("(player)", duelRequest.getSender().getName()))
                .map(str -> str.replace("(kit)", duelRequest.getDuelKit() == DuelKitType.CUSTOM ? duelRequest.getDuelKit().getMessage() : duelRequest.getKitModel().getDisplayName()))
                .map(str -> str.replace("(numGames)", duelRequest.getNumGames() + ""))
                .map(str -> str.replace("(arena)", duelRequest.getArena().getArenaName()))
                .forEach(text -> duelRequest.getReceiver().sendMessage(text));
        Component message = Component.text().append(accept).append(Component.text(" ")).append(decline).asComponent();
        receiver.sendMessage(message);
    }

    /**
     * Removes a duel request from the system.
     *
     * @param duelRequest The DuelRequest to remove.
     */
    @Override
    public void removeRequest(DuelRequest duelRequest) {
        this.requestsBucket.removeRequest(duelRequest);
    }

    /**
     * Initiates a fight based on the provided duel request.
     *
     * @param duelRequest The duel request to start the fight with.
     */
    @Override
    public void startFight(DuelRequest duelRequest) {
        // Сразу получаем игроков чтоб потом не ебатся
        Player sender = duelRequest.getSenderParty() == null ? duelRequest.getSender() : Bukkit.getPlayer(duelRequest.getSenderParty().getOwner());
        Player receiver = duelRequest.getReceiverParty() == null ? duelRequest.getReceiver() : Bukkit.getPlayer(duelRequest.getReceiverParty().getOwner());
        assert sender != null;
        assert receiver != null;
        // Получаем арену
        ArenaModel arenaModel = duelRequest.getArena();
        if (arenaModel == null) {
            PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), sender, receiver);
            return;
        }
        if (this.arenaCore.getArenaAPI().isBusyArena(arenaModel)) {
            arenaModel = this.getFreeArenaName(arenaModel.getDisplayName());
            if (arenaModel == null) {
                PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), sender, receiver);
                return;
            }
        }
        boolean isLocationError = false;
        if (arenaModel.isFfa()) {
            for (EntityPosition position : arenaModel.getFfaPositions().values()) {
                if (position.getWorld() == null) {
                    this.plugin.getLogger().info("Мир не найден! Arena: " + arenaModel.getArenaName() + " World: " + position.getWorldName());
                    isLocationError = true;
                }
            }
        } else {
            if (arenaModel.getOnePosition() == null || arenaModel.getOnePosition().getWorld() == null
            || arenaModel.getTwoPosition() == null || arenaModel.getTwoPosition().getWorld() == null
            || arenaModel.getThreePosition() == null || arenaModel.getThreePosition().getWorld() == null
            || arenaModel.getFourPosition() == null || arenaModel.getFourPosition().getWorld() == null) {
                this.plugin.getLogger().info("Arena: " + arenaModel.getArenaName() + " ошибки в локациях. Возможно их не существует");
                isLocationError = true;
            }
        }
        if (isLocationError) {
            int count = 5;
            ArenaModel oldArena = arenaModel.clone();
            while (count != 0) {
                if (arenaModel == null) {
                    arenaModel = this.getFreeArena();
                    continue;
                }
                if (!arenaModel.equals(oldArena)) break;
                arenaModel = this.getFreeArena();
                count--;
            }
            if (count == 0) {
                PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), sender, receiver);
                return;
            }
        }
        // Создаем файт
        DuelFightModel duelFightModel = new DuelFightModel(sender, receiver, duelRequest.getNumGames(), duelRequest.getKitModel(), arenaModel);
        if (duelRequest.getSignModel() != null) {
            duelFightModel.setSignModel(duelRequest.getSignModel());
        }
        PlayerUtil.healPlayers(sender, receiver);
        PlayerUtil.clearEffects(sender, receiver);
        PlayerUtil.setGameModeAndFly(sender, receiver);
        this.fightBucket.addFight(duelFightModel);
        this.arenaCore.getArenaAPI().addBusyArena(duelFightModel.getArenaModel());
        // Телепортируем игроков на арену
        if (duelRequest.getArena().isFfa()) {
            PartyModel receiverParty = duelRequest.getReceiverParty();
            PartyModel senderParty = duelRequest.getSenderParty();
            duelFightModel.setReceiverParty(receiverParty);
            duelFightModel.setSenderParty(senderParty);
            PartyAPI partyAPI = PartyCore.INSTANCE.getPartyAPI();
            partyAPI.teleportToArena(duelRequest);
            List<Player> senderPlayers = PlayerUtil.convertListUUID(senderParty.getPlayers());
            List<Player> receiverPlayers = PlayerUtil.convertListUUID(receiverParty.getPlayers());
            this.preparationToFight(senderPlayers);
            this.preparationToFight(receiverPlayers);
            duelFightModel.getKitModel().giveItemPlayers(sender, receiver);
            duelFightModel.getKitModel().giveItemPlayers(receiverPlayers);
            duelFightModel.getKitModel().giveItemPlayers(senderPlayers);
            partyAPI.addFightParty(senderParty, receiverParty);
            DuelStartFightEvent.create(senderParty, receiverParty, duelFightModel).call();
        } else {
            sender.teleport(duelRequest.getArena().getOnePosition().toLocation());
            receiver.teleport(duelRequest.getArena().getThreePosition().toLocation());
            this.preparationToFight(sender, receiver);
            duelFightModel.getKitModel().giveItemPlayers(sender, receiver);
            DuelStartFightEvent.create(sender, receiver, duelFightModel).call();
        }
        duelFightModel.setBukkitTask(new DuelScheduler(TimeUtil.parseTime(this.settings.getDurationFight(), TimeUnit.MINUTES), duelFightModel));
        this.duelTeleportBucket.add(new DuelTeleportScheduler(duelFightModel));
        sender.getNearbyEntities(100, 100, 100).stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
    }

    @Override
    public void startFightFour(Player player, Player player2, Player player3, Player player4, DuelRequest duelRequest) {
        ArenaModel arenaModel = duelRequest.getArena();
        if (arenaModel == null) {
            PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), player, player2, player3, player4);
            return;
        }
        if (this.arenaCore.getArenaAPI().isBusyArena(arenaModel)) {
            arenaModel = this.getFreeArenaName(arenaModel.getDisplayName());
            if (arenaModel == null) {
                PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), player, player2, player3, player4);
                return;
            }
        }
        boolean isLocationError = false;
        if (arenaModel.isFfa()) {
            for (EntityPosition position : arenaModel.getFfaPositions().values()) {
                if (position.getWorld() == null) {
                    this.plugin.getLogger().info("Мир не найден! Arena: " + arenaModel.getArenaName() + " World: " + position.getWorldName());
                    isLocationError = true;
                }
            }
        } else {
            if (arenaModel.getOnePosition() == null || arenaModel.getOnePosition().getWorld() == null
                    || arenaModel.getTwoPosition() == null || arenaModel.getTwoPosition().getWorld() == null
                    || arenaModel.getThreePosition() == null || arenaModel.getThreePosition().getWorld() == null
                    || arenaModel.getFourPosition() == null || arenaModel.getFourPosition().getWorld() == null) {
                this.plugin.getLogger().info("Arena: " + arenaModel.getArenaName() + " ошибки в локациях. Возможно их не существует");
                isLocationError = true;
            }
        }
        if (isLocationError) {
            int count = 5;
            ArenaModel oldArena = arenaModel.clone();
            while (count != 0) {
                if (arenaModel == null) {
                    arenaModel = this.getFreeArena();
                    continue;
                }
                if (!arenaModel.equals(oldArena)) break;
                arenaModel = this.getFreeArena();
                count--;
            }
            if (count == 0) {
                PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), player, player2, player3, player4);
                return;
            }
        }
        DuelFightModel duelFightModel = new DuelFightModel(player, player3, duelRequest.getNumGames(), duelRequest.getKitModel(), arenaModel);
        if (duelRequest.getSignModel() != null) {
            duelFightModel.setSignModel(duelRequest.getSignModel());
        }
        duelFightModel.setPlayer2(player2);
        duelFightModel.setPlayer4(player4);
        PlayerUtil.healPlayers(player, player2, player3, player4);
        PlayerUtil.clearEffects(player, player2, player3, player4);
        PlayerUtil.setGameModeAndFly(player, player2, player3, player4);
        player.teleport(arenaModel.getFfaPositions().get(1).toLocation());
        player2.teleport(arenaModel.getFfaPositions().get(2).toLocation());
        player3.teleport(arenaModel.getFfaPositions().get(11).toLocation());
        player4.teleport(arenaModel.getFfaPositions().get(12).toLocation());
        duelFightModel.getKitModel().giveItemPlayers(player, player2, player3, player4);
        duelFightModel.setBukkitTask(new DuelScheduler(TimeUtil.parseTime(this.settings.getDurationFight(), TimeUnit.MINUTES), duelFightModel));
        this.fightBucket.addFight(duelFightModel);
        this.duelTeleportBucket.add(new DuelTeleportScheduler(duelFightModel));
        player.getNearbyEntities(100, 100, 100).stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
    }

    /**
     * Moves to the next round of the ongoing fight.
     *
     * @param duelFight The DuelFightModel representing the ongoing fight.
     */
    @Override
    public void nextRound(DuelFightModel duelFight) {
        if (this.fightBucket.getDuelFights().stream().noneMatch(duelFight::equals)) {
            return;
        }
        Player sender = duelFight.getSender();
        Player receiver = duelFight.getReceiver();
        ArenaModel arenaModel = duelFight.getArenaModel();
        if (sender.getGameMode() != GameMode.SURVIVAL) {
            sender.setGameMode(GameMode.SURVIVAL);
        }
        if (receiver.getGameMode() != GameMode.SURVIVAL) {
            receiver.setGameMode(GameMode.SURVIVAL);
        }
        sender.setFlying(false);
        receiver.setFlying(false);
        if (arenaModel.isFfa()) {
            PartyModel receiverParty = duelFight.getReceiverParty();
            PartyModel senderParty = duelFight.getSenderParty();
            if (receiverParty != null && senderParty != null) {
                PartyAPI partyAPI = PartyCore.INSTANCE.getPartyAPI();
                partyAPI.teleportToArena(duelFight);
                sender.setFireTicks(0);
                receiver.setFireTicks(0);
                this.preparationToFight(receiverParty, senderParty);
                List<Player> senderPlayers = PlayerUtil.convertListUUID(senderParty.getPlayers()).stream().filter(player -> !duelFight.getSpectates().contains(player.getUniqueId())).collect(Collectors.toList());
                List<Player> receiverPlayers = PlayerUtil.convertListUUID(receiverParty.getPlayers()).stream().filter(player -> !duelFight.getSpectates().contains(player.getUniqueId())).collect(Collectors.toList());
                duelFight.getKitModel().giveItemPlayers(sender, receiver);
                duelFight.getKitModel().giveItemPlayers(receiverPlayers);
                duelFight.getKitModel().giveItemPlayers(senderPlayers);
            } else {
                Player player2 = duelFight.getPlayer2();
                Player player4 = duelFight.getPlayer4();
                PlayerUtil.healPlayers(sender, receiver, player2, player4);
                PlayerUtil.clearEffects(sender, receiver, player2, player4);
                duelFight.getKitModel().giveItemPlayers(sender, receiver, player2, player4);
                Map<Integer, EntityPosition> pos = duelFight.getArenaModel().getFfaPositions();
                sender.teleport(pos.get(1).toLocation());
                player2.teleport(pos.get(2).toLocation());
                receiver.teleport(pos.get(11).toLocation());
                player4.teleport(pos.get(12).toLocation());
            }
        } else {
            sender.teleport(arenaModel.getOnePosition().toLocation());
            receiver.teleport(arenaModel.getThreePosition().toLocation());
            PlayerUtil.healPlayers(sender, receiver);
            PlayerUtil.clearEffects(sender, receiver);
            duelFight.getKitModel().giveItemPlayers(sender, receiver);
        }
        duelFight.getSpectates().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(spectator -> spectator.teleport(arenaModel.getSpectatorPosition().toLocation()));
        sender.getNearbyEntities(100, 100, 100).stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        if (arenaModel.isBreaking()) {
            this.arenaCore.getArenaAPI().restoreArena(arenaModel);
        }
        duelFight.getBukkitTask().updateTime(duelFight);
        this.duelTeleportBucket.add(new DuelTeleportScheduler(duelFight));
    }

    /**
     * Stops the ongoing fight and determines the winner and loser.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param winner         The player who won the fight.
     * @param loser          The player who lost the fight.
     */
    @Override
    public void stopFight(DuelFightModel duelFightModel, Player winner, Player loser) {
        this.getTeleportSchedulerFromFight(duelFightModel).ifPresent(teleportScheduler -> {
            this.duelTeleportBucket.remove(teleportScheduler);
            this.removeNoMove(duelFightModel.getReceiver());
            this.removeNoMove(duelFightModel.getSender());
            if (duelFightModel.getArenaModel().isFfa()) {
                if (duelFightModel.getSenderParty() != null && duelFightModel.getReceiverParty() != null) {
                    PlayerUtil.convertListUUID(duelFightModel.getSenderParty().getPlayers()).forEach(this::removeNoMove);
                    PlayerUtil.convertListUUID(duelFightModel.getReceiverParty().getPlayers()).forEach(this::removeNoMove);
                } else {
                    this.removeNoMove(duelFightModel.getPlayer4());
                    this.removeNoMove(duelFightModel.getPlayer2());
                }
            }
            teleportScheduler.cancel();
        });
        this.fightBucket.removeFight(duelFightModel);
        ArenaModel arenaModel = duelFightModel.getArenaModel();
        StringBuilder winners = new StringBuilder();
        StringBuilder losers = new StringBuilder();
        List<Player> winnerParty = new ArrayList<>();
        List<Player> loserParty = new ArrayList<>();
        if (winner != null) {
            winners.append(winner.getName());
        }
        if (loser != null) {
            losers.append(loser.getName());
        }
        if (arenaModel.isFfa()) {
            if (duelFightModel.getReceiverParty() != null && duelFightModel.getSenderParty() != null) {
                PartyAPI partyAPI = PartyCore.INSTANCE.getPartyAPI();
                partyAPI.removeFightParty(duelFightModel.getReceiverParty(), duelFightModel.getSenderParty());
                if (winner != null) {
                    for (Player player : PlayerUtil.convertListUUID(Objects.requireNonNull(partyAPI.getPartyModelFromPlayer(winner)).getPlayers())) {
                        winnerParty.add(player);
                        winners.append(" ").append(player.getName());
                    }
                }
                if (loser != null) {
                    for (Player player : PlayerUtil.convertListUUID(Objects.requireNonNull(partyAPI.getPartyModelFromPlayer(loser)).getPlayers())) {
                        loserParty.add(player);
                        losers.append(" ").append(player.getName());
                    }
                }
            } else {
                if (winner != null) {
                    if (winner.equals(duelFightModel.getSender())) {
                        winners.append(" ").append(duelFightModel.getPlayer2().getName());
                    } else {
                        winners.append(" ").append(duelFightModel.getPlayer4().getName());
                    }
                }
                if (loser != null) {
                    if (loser.equals(duelFightModel.getSender())) {
                        losers.append(" ").append(duelFightModel.getPlayer2().getName());
                    } else {
                        winners.append(" ").append(duelFightModel.getPlayer4().getName());
                    }
                }
            }
        }

        this.arenaCore.getArenaAPI().removeBusyArena(arenaModel);
        Player receiver = duelFightModel.getReceiver();
        Player sender = duelFightModel.getSender();
        this.messageConfiguration.getMessages("endFight")
                .stream()
                .map(str -> str.replace("(playersHealth)", winner == null ? "" : this.getHealthPlayers(duelFightModel, loser)))
                .map(str -> str.replace("(winner)", winner == null ? this.messageConfiguration.getMessage("replaceWinnerNull") : winners.toString()))
                .map(str -> str.replace("(loser)", loser == null ? this.messageConfiguration.getMessage("replaceLoserNull") : losers.toString()))
                .map(str -> str.replace("(kit)", ColorUtil.color(duelFightModel.getKitModel().getDisplayName())))
                .map(str -> str.replace("(numGames)", String.valueOf(duelFightModel.getNumGames())))
                .forEach(text -> {
                    PlayerUtil.sendMessage(text, receiver, sender);
                    if (arenaModel.isFfa()) {
                        if (duelFightModel.getReceiverParty() != null && duelFightModel.getSenderParty() != null) {
                            winnerParty.forEach(player -> player.sendMessage(text));
                            loserParty.forEach(player -> player.sendMessage(text));
                        } else {
                            PlayerUtil.sendMessage(text, duelFightModel.getPlayer2(), duelFightModel.getPlayer4());
                        }
                    }
                });
        // Телепортируем на спавн и отправляем сообщение о конце битвы
        Location location = this.getRandomSpawn();
        if (arenaModel.isFfa()) {
            if (duelFightModel.getReceiverParty() != null && duelFightModel.getSenderParty() != null) {
                winnerParty.forEach(player -> {
                    player.teleport(location);
                    PlayerUtil.clearEffects(player);
                    PlayerUtil.healPlayers(player);
                    if (player.getGameMode() != GameMode.SURVIVAL) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                    PartyCore.INSTANCE.getPartyAPI().giveStartItems(player);
                });
                loserParty.forEach(player -> {
                    player.teleport(location);
                    PlayerUtil.clearEffects(player);
                    PlayerUtil.healPlayers(player);
                    if (player.getGameMode() != GameMode.SURVIVAL) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                    PartyCore.INSTANCE.getPartyAPI().giveStartItems(player);
                });
            } else {
                Player player2 = duelFightModel.getPlayer2();
                Player player4 = duelFightModel.getPlayer4();
                player2.teleport(location);
                player4.teleport(location);
                PlayerUtil.healPlayers(player2, player4);
                PlayerUtil.clearEffects(player2, player4);
                if (player2.getGameMode() != GameMode.SURVIVAL) {
                    player2.setGameMode(GameMode.SURVIVAL);
                }
                if (player4.getGameMode() != GameMode.SURVIVAL) {
                    player4.setGameMode(GameMode.SURVIVAL);
                }
                PartyAPI partyAPI = PartyCore.INSTANCE.getPartyAPI();
                if (partyAPI.isPartyPlayer(player2)) {
                    partyAPI.giveStartItems(player2);
                } else {
                    this.giveStartItems(player2);
                }
                if (partyAPI.isPartyPlayer(player4)) {
                    partyAPI.giveStartItems(player4);
                } else {
                    this.giveStartItems(player4);
                }
            }
        }
        if (receiver != null) {
            receiver.teleport(location);
            PlayerUtil.clearEffects(receiver);
            PlayerUtil.healPlayers(receiver);
            if (receiver.getGameMode() != GameMode.SURVIVAL) {
                receiver.setGameMode(GameMode.SURVIVAL);
            }
            if (arenaModel.isFfa() && PartyCore.INSTANCE.getPartyAPI().isPartyPlayer(receiver)) {
                PartyCore.INSTANCE.getPartyAPI().giveStartItems(receiver);
            } else {
                this.giveStartItems(receiver);
            }
        }
        if (sender != null) {
            sender.teleport(location);
            PlayerUtil.clearEffects(sender);
            PlayerUtil.healPlayers(sender);
            if (sender.getGameMode() != GameMode.SURVIVAL) {
                sender.setGameMode(GameMode.SURVIVAL);
            }
            if (arenaModel.isFfa() && PartyCore.INSTANCE.getPartyAPI().isPartyPlayer(sender)) {
                PartyCore.INSTANCE.getPartyAPI().giveStartItems(sender);
            } else {
                this.giveStartItems(sender);
            }
        }
        if (duelFightModel.getSignModel() != null) {
            SignCore.INSTANCE.getSignAPI().removeSignFight(duelFightModel.getSignModel());
        }
        duelFightModel.getBukkitTask().cancel();
        if (!duelFightModel.getSpectates().isEmpty()) {
            try {
                Set<Player> spectators = duelFightModel.getSpectates().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
                spectators.forEach(player -> this.removeSpectate(player, duelFightModel, false));
            } catch (ConcurrentModificationException ignored) {
                Iterator<UUID> spectatorsUUID = duelFightModel.getSpectates().iterator();
                while (spectatorsUUID.hasNext()) {
                    Player player = Bukkit.getPlayer(spectatorsUUID.next());
                    if (player == null) {
                        continue;
                    }
                    this.removeSpectate(player, duelFightModel, false);
                }
            }
        }
        DuelStopFightEvent.create(duelFightModel.getSender(), duelFightModel.getReceiver(), winner, loser, duelFightModel).call();
    }

    private String getHealthPlayers(DuelFightModel duelFightModel, Player loser) {
        StringBuilder sb = new StringBuilder();
        if (!duelFightModel.getArenaModel().isFfa()) {
            return duelFightModel.getSender().equals(loser) ? sb.append(duelFightModel.getReceiver().getDisplayName()).append(" ").append(duelFightModel.getReceiver().getHealth()).toString() : sb.append(duelFightModel.getSender().getDisplayName()).append(" ").append(duelFightModel.getSender().getHealth()).toString();
        }
        if (duelFightModel.getPlayer2() != null && duelFightModel.getPlayer4() != null) {
            if (duelFightModel.getPlayer2().equals(loser) || duelFightModel.getSender().equals(loser)) {
                if (duelFightModel.getReceiver().getGameMode() != GameMode.SPECTATOR) {
                    sb.append(duelFightModel.getReceiver().getDisplayName()).append(" ").append(duelFightModel.getReceiver().getHealth());
                }
                if (duelFightModel.getPlayer4().getGameMode() != GameMode.SPECTATOR) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(duelFightModel.getPlayer4().getDisplayName()).append(" ").append(duelFightModel.getPlayer4().getHealth());
                }
                return sb.toString();
            }
            if (duelFightModel.getSender().getGameMode() != GameMode.SPECTATOR) {
                sb.append(duelFightModel.getSender().getDisplayName()).append(" ").append(duelFightModel.getSender().getHealth());
            }
            if (duelFightModel.getPlayer2().getGameMode() != GameMode.SPECTATOR) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(duelFightModel.getPlayer2().getDisplayName()).append(" ").append(duelFightModel.getPlayer2().getHealth());
            }
            return sb.toString();
        }
        if (duelFightModel.getReceiverParty() != null && duelFightModel.getSenderParty() != null) {
            PartyModel winnerParty;
            if (duelFightModel.getReceiverParty().getOwner().equals(loser.getUniqueId()) || duelFightModel.getReceiverParty().getPlayers().contains(loser.getUniqueId())) {
                winnerParty = duelFightModel.getSenderParty();
            } else {
                winnerParty = duelFightModel.getReceiverParty();
            }
            Player winnerOwner = Bukkit.getPlayer(winnerParty.getOwner());
            if (winnerOwner != null && winnerOwner.getGameMode() != GameMode.SPECTATOR) {
                sb.append(winnerOwner.getDisplayName()).append(" ").append(winnerOwner.getHealth());
            }
            PlayerUtil.convertListUUID(winnerParty.getPlayers()).stream().filter(player -> player.getGameMode() != GameMode.SPECTATOR).forEach(player -> {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(player.getDisplayName()).append(" ").append(player.getHealth());
            });
            return sb.toString();
        }
        return "";
    }

    /**
     * Retrieves a free arena available for a fight.
     *
     * @return A free ArenaModel for a fight, or null if none is available.
     */
    @Override
    public @Nullable ArenaModel getFreeArena() {
        return this.arenaCore.getArenas().getArenas()
                .stream()
                .filter(arenaModel -> !arenaModel.isFfa())
                .filter(arenaModel -> !this.arenaCore.getArenaBusyBucket().getArenas().contains(arenaModel))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a free arena with the specified name.
     *
     * @param name The name of the arena to retrieve.
     * @return A free ArenaModel with the specified name, or null if none is available.
     */
    @Override
    public @Nullable ArenaModel getFreeArenaName(String name) {
        for (ArenaModel arena : this.arenaCore.getArenaAPI().getArenasFromName(name)) {
            if (this.arenaCore.getArenaBusyBucket().getArenas().isEmpty()) {
                return arena;
            }
            for (ArenaModel bussyArena : this.arenaCore.getArenaBusyBucket().getArenas()) {
                if (!arena.equals(bussyArena)) return arena;
            }
        }
        return null;
    }

    @Override
    public @Nullable ArenaModel getFreeArenaFFA() {
        return this.arenaCore.getArenas().getArenas()
                .stream()
                .filter(arenaModel -> !this.arenaCore.getArenaBusyBucket().getArenas().contains(arenaModel))
                .filter(ArenaModel::isFfa)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a list of duel requests received by the specified player.
     *
     * @param receiver The player who received the requests.
     * @return A list of DuelRequest objects received by the player.
     */
    @Override
    public @Nullable List<DuelRequest> getRequestsFromReceiver(Player receiver) {
        return this.requestsBucket.getRequests().stream().filter(duelRequest -> duelRequest.getReceiver().getUniqueId().equals(receiver.getUniqueId())).collect(Collectors.toList());
    }

    /**
     * Retrieves the winner of a fight based on the provided DuelFightModel and loser.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param loser          The player who lost the fight.
     * @return The winner of the fight.
     */
    @Override
    public Player getWinnerFromFight(DuelFightModel duelFightModel, Player loser) {
        return duelFightModel.getReceiver().getUniqueId().equals(loser.getUniqueId()) ? duelFightModel.getSender() : duelFightModel.getReceiver();
    }

    /**
     * Retrieves the loser of a fight based on the provided DuelFightModel and winner.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param winner         The player who won the fight.
     * @return The loser of the fight.
     */
    @Override
    public Player getLoserFromFight(DuelFightModel duelFightModel, Player winner) {
        return duelFightModel.getReceiver().getUniqueId().equals(winner.getUniqueId()) ? duelFightModel.getSender() : duelFightModel.getReceiver();
    }

    /**
     * Retrieves the opponent of a player in the provided DuelFightModel.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param player         The player for whom to retrieve the opponent.
     * @return The opponent of the player in the fight.
     */
    @Override
    public Player getOpponentFromFight(DuelFightModel duelFightModel, Player player) {
        return duelFightModel.getReceiver().getUniqueId().equals(player.getUniqueId()) ? duelFightModel.getSender() : duelFightModel.getReceiver();
    }

    /**
     * Retrieves the opponent of a player in the ongoing fight.
     *
     * @param player The player for whom to retrieve the opponent.
     * @return The opponent of the player in the ongoing fight.
     */
    @Nullable
    @Override
    public Player getOpponentFromFight(Player player) {
        return this.getOpponentFromFight(Objects.requireNonNull(this.getFightModelFromPlayer(player)), player);
    }

    /**
     * Retrieves a duel request sent by the specified sender to the specified receiver.
     *
     * @param sender   The sender of the request.
     * @param receiver The receiver of the request.
     * @return The DuelRequest sent by the sender to the receiver, if found; otherwise, null.
     */
    @Override
    public @Nullable DuelRequest getRequestFromSender(Player sender, Player receiver) {
        return this.requestsBucket.getRequests()
                .stream()
                .filter(request -> request.getSender().getUniqueId().equals(sender.getUniqueId()))
                .filter(request -> request.getReceiver().getUniqueId().equals(receiver.getUniqueId()))
                .findFirst()
                .orElse(null);
    }


    /**
     * Saves a custom kit for the specified player.
     *
     * @param player  The player saving the kit.
     * @param kitName The name of the kit to save.
     */
    @Override
    public void saveKitServer(Player player, String kitName) {
        Map<Integer, ItemBuilder> items = new HashMap<>();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getItemInMainHand();
        for (int i = 0 ; i < inventory.getSize() ; i++) {
            ItemStack inventoryItem = inventory.getItem(i);
            if (inventoryItem == null || inventoryItem.getType().isAir()) {
                continue;
            }
            if (!itemStack.getType().isAir()) {
                itemStack = inventoryItem;
            }
            items.put(i, ItemBuilder.builder().fromItemStack(inventoryItem));
        }
        KitModel kitModel = KitModel.create(kitName, getFreeSlotKit(), new ArrayList<>(), itemStack.getType().isAir() ? Material.PAPER : itemStack.getType(), items);
        this.kitConfig.getKits().add(kitModel);
        this.kitConfig.save();
        player.sendMessage("Вы успешно сохранили кит " + kitName);
    }

    /**
     * Checks if a kit with the specified name exists.
     *
     * @param kitName The name of the kit to check.
     * @return True if a kit with the specified name exists, otherwise false.
     */
    @Override
    public boolean isKitNameContains(String kitName) {
        return this.kitConfig.getKits().stream().map(KitModel::getDisplayName).anyMatch(name -> name.equalsIgnoreCase(kitName));
    }

    /**
     * Gets a free slot for a kit.
     *
     * @return The index of a free slot for a kit, or -1 if none is available.
     */
    @Override
    public int getFreeSlotKit() {
        for (int i = 0 ; i < this.duelCore.getDuelConfig().getChoiceKitMenu().getRequestKit().getSize() ; i++) {
            for (ItemBuilder builder : this.duelCore.getDuelConfig().getChoiceKitMenu().getRequestKit().getKits().keySet()) {
                if (i != builder.getSlot()) return i;
            }
        }
        return -1;
    }

    /**
     * Retrieves a random spawn location.
     *
     * @return A random Location object representing a spawn location.
     */
    @Override
    public Location getRandomSpawn() {
        return this.settings.getSpawns().get(ThreadLocalRandom.current().nextInt(this.settings.getSpawns().size())).toLocation();
    }

    /**
     * Gives starting items to the specified player.
     *
     * @param player The player to whom to give starting items.
     */
    @Override
    public void giveStartItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(this.settings.getCreateCustomKit().getSlot(), this.settings.getCreateCustomKit().build());
        player.updateInventory();
    }

    /**
     * Retrieves a random KitModel from the available kits.
     *
     * @return A random KitModel.
     */
    @Override
    public KitModel getRandomKit() {
        List<KitModel> kit = new ArrayList<>(this.duelCore.getDuelConfig().getChoiceKitMenu().getRequestKit().getKits().values());
        return kit.get(ThreadLocalRandom.current().nextInt(kit.size()));
    }

    /**
     * Adds the specified player to the list of players who cannot move during a fight.
     *
     * @param player The player to add.
     */
    @Override
    public void addNoMove(Player player) {
        if (!isNoMovePlayer(player)) {
            this.duelMoveBucket.add(player);
        }
    }

    /**
     * Removes the specified player from the list of players who cannot move during a fight.
     *
     * @param player The player to remove.
     */
    @Override
    public void removeNoMove(Player player) {
        if (isNoMovePlayer(player)) {
            this.duelMoveBucket.remove(player);
        }
    }

    /**
     * Checks if the specified player is restricted from moving during a fight.
     *
     * @param player The player to check.
     * @return True if the player is restricted from moving, otherwise false.
     */
    @Override
    public boolean isNoMovePlayer(Player player) {
        return this.duelMoveBucket.contains(player);
    }

    /**
     * Adds the specified player as a spectator in the provided duel fight.
     *
     * @param player       The player to add as a spectator.
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     */
    @Override
    public void addSpectate(Player player, DuelFightModel duelFightModel) {
        // Добавляем игрока в лист спектаторов в файте
        duelFightModel.getSpectates().add(player.getUniqueId());
        // Ставим режим игры + телепортируем
        player.teleport(duelFightModel.getArenaModel().getSpectatorPosition().toLocation());
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        // Отправка сообщения
        PlayerUtil.sendMessage(this.messageConfiguration.getMessage("startSpectate").replace("(player)", player.getName()), duelFightModel.getReceiver(), duelFightModel.getSender());
    }

    /**
     * Removes the specified player from being a spectator in the provided duel fight.
     *
     * @param player       The player to remove from being a spectator.
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param fighting     True if the players are currently fighting, otherwise false.
     */
    @Override
    public void removeSpectate(Player player, DuelFightModel duelFightModel, boolean fighting) {
        // Убираем игрока из листа спектаторов в файте
        duelFightModel.getSpectates().remove(player.getUniqueId());
        // Телепортируем в лобби и ставим режим выживания
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(this.getRandomSpawn());
        // Если они бьются то отправляем сообщение
        if (fighting) {
            PlayerUtil.sendMessage(this.messageConfiguration.getMessage("stopSpectate").replace("(player)", player.getName()), duelFightModel.getSender(), duelFightModel.getReceiver());
        }
    }

    /**
     * Checks if the specified player is currently a spectator in any ongoing fight.
     *
     * @param player The player to check.
     * @return True if the player is a spectator, otherwise false.
     */
    @Override
    public boolean isSpectate(Player player) {
        return getDuelFightModelFromSpectator(player) != null;
    }

    /**
     * Retrieves the DuelFightModel associated with the specified spectator player.
     *
     * @param player The player who is spectating.
     * @return The DuelFightModel associated with the spectator player, or null if not spectating.
     */
    @Override
    public @Nullable DuelFightModel getDuelFightModelFromSpectator(Player player) {
        for (DuelFightModel fightModel : this.fightBucket.getDuelFights()) {
            for (UUID spectator : fightModel.getSpectates()) {
                if (spectator.equals(player.getUniqueId())) return fightModel;
            }
        }
        return null;
    }


    /**
     * Prepares a list of players for a fight by setting their game modes, clearing effects, and healing them.
     *
     * @param players The list of players to prepare for the fight.
     */
    @Override
    public void preparationToFight(List<Player> players) {
        players.forEach(player -> {
            player.setFlying(false);
            if (this.isSpectate(player)) {
                this.removeSpectate(player, Objects.requireNonNull(this.getDuelFightModelFromSpectator(player)), true);
            }
            if (player.getGameMode() != GameMode.SURVIVAL) {
                player.setGameMode(GameMode.SURVIVAL);
            }
            player.setFireTicks(0);
            PlayerUtil.clearEffects(player);
            PlayerUtil.healPlayers(player);
        });
    }

    /**
     * Prepares a group of players for a fight by setting their game modes, clearing effects, and healing them.
     *
     * @param players The group of players to prepare for the fight.
     */
    @Override
    public void preparationToFight(Player... players) {
        this.preparationToFight(Arrays.asList(players));
    }

    /**
     * Prepares two party models for a fight by setting their game modes, clearing effects, and healing them.
     *
     * @param senderParty   The party model of the sender.
     * @param receiverParty The party model of the receiver.
     */
    @Override
    public void preparationToFight(PartyModel senderParty, PartyModel receiverParty) {
        this.preparationToFight(PlayerUtil.convertListUUID(senderParty.getPlayers()));
        this.preparationToFight(PlayerUtil.convertListUUID(receiverParty.getPlayers()));
        this.preparationToFight(Bukkit.getPlayer(senderParty.getOwner()), Bukkit.getPlayer(receiverParty.getOwner()));
    }

    /**
     * Retrieves the teleport scheduler associated with the provided duel fight.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @return An Optional containing the DuelTeleportScheduler if found, otherwise empty.
     */
    @Override
    public Optional<DuelTeleportScheduler> getTeleportSchedulerFromFight(DuelFightModel duelFightModel) {
        return this.duelTeleportBucket.getTeleportSchedulerFromFight(duelFightModel);
    }
}

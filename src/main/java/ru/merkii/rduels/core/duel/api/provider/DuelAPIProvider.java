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
import ru.merkii.rduels.core.duel.config.DuelConfig;
import ru.merkii.rduels.core.duel.event.*;
import ru.merkii.rduels.core.duel.model.*;
import ru.merkii.rduels.core.duel.schedualer.*;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.sign.SignCore;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.KitModel;
import ru.merkii.rduels.util.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DuelAPIProvider implements DuelAPI {

    private final RDuels plugin = RDuels.getInstance();
    private final MessageConfiguration messageConfiguration = this.plugin.getPluginMessage();
    private final Settings settings = this.plugin.getSettings();
    private final KitConfig kitConfig = this.plugin.getKitConfig();
    private final ArenaCore arenaCore = ArenaCore.INSTANCE;
    private final DuelCore duelCore = DuelCore.INSTANCE;
    private final DuelFightBucket fightBucket = this.duelCore.getDuelFightBucket();
    private final DuelRequestsBucket requestsBucket = this.duelCore.getDuelRequestsBucket();
    private final DuelMoveBucket duelMoveBucket = new DuelMoveBucket();
    private final DuelTeleportBucket duelTeleportBucket = new DuelTeleportBucket();

    @Override
    public KitModel getKitFromName(String kitName) {
        return this.kitConfig.getKits().stream().filter(model -> model.getDisplayName().equalsIgnoreCase(kitName)).findFirst().orElse(null);
    }

    @Override
    public boolean isFightPlayer(Player player) {
        for (DuelFightModel fightModel : this.fightBucket.getDuelFights()) {
            if (fightModel.getArenaModel().isFfa() && (fightModel.getReceiverParty() != null && fightModel.getSenderParty() != null ? fightModel.getReceiverParty().getPlayers().contains(player.getUniqueId()) || Objects.requireNonNull(fightModel.getSenderParty()).getPlayers().contains(player.getUniqueId()) : fightModel.getPlayer2().equals(player) || fightModel.getPlayer4().equals(player))) {
                return true;
            }
            if (!fightModel.getReceiver().equals(player) && !fightModel.getSender().equals(player)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public DuelFightModel getFightModelFromPlayer(Player player) {
        for (DuelFightModel fightModel : this.fightBucket.getDuelFights()) {
            if (fightModel.getArenaModel().isFfa() && (fightModel.getReceiverParty() != null && fightModel.getSenderParty() != null ? fightModel.getReceiverParty().getPlayers().contains(player.getUniqueId()) || Objects.requireNonNull(fightModel.getSenderParty()).getPlayers().contains(player.getUniqueId()) : fightModel.getPlayer2().equals(player) || fightModel.getPlayer4().equals(player))) {
                return fightModel;
            }
            if (!fightModel.getReceiver().getUniqueId().equals(player.getUniqueId()) && !fightModel.getSender().getUniqueId().equals(player.getUniqueId())) continue;
            return fightModel;
        }
        return null;
    }

    @Override
    public void addRequest(DuelRequest duelRequest) {
        this.requestsBucket.addRequest(duelRequest);
        Player sender = duelRequest.getSenderParty() != null ? Bukkit.getPlayer(duelRequest.getSenderParty().getOwner()) : duelRequest.getSender();
        Player receiver = duelRequest.getReceiverParty() != null ? Bukkit.getPlayer(duelRequest.getReceiverParty().getOwner()) : duelRequest.getReceiver();
        assert (sender != null);
        assert (receiver != null);
        sender.sendMessage(this.messageConfiguration.getMessage("requestSender").replace("(player)", duelRequest.getReceiver().getName()));
        Component accept = Component.text(this.messageConfiguration.getMessage("acceptButton")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/duel yes " + sender.getName()));
        Component decline = Component.text(this.messageConfiguration.getMessage("declineButton")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/duel no " + sender.getName()));
        this.messageConfiguration.getMessages("requestReceiver").stream().map(str -> str.replace("(player)", duelRequest.getSender().getName())).map(str -> str.replace("(kit)", duelRequest.getDuelKit() == DuelKitType.CUSTOM ? duelRequest.getDuelKit().getMessage() : duelRequest.getKitModel().getDisplayName())).map(str -> str.replace("(numGames)", duelRequest.getNumGames() + "")).map(str -> str.replace("(arena)", duelRequest.getArena().getArenaName())).forEach(text -> duelRequest.getReceiver().sendMessage((String)text));
        Component message = (Component.text().append(accept).append(Component.text(" ")).append(decline)).asComponent();
        receiver.sendMessage(message);
    }

    @Override
    public void removeRequest(DuelRequest duelRequest) {
        this.requestsBucket.removeRequest(duelRequest);
    }

    @Override
    public void startFight(DuelRequest duelRequest) {
        Player receiver;
        Player sender = duelRequest.getSenderParty() == null ? duelRequest.getSender() : Bukkit.getPlayer(duelRequest.getSenderParty().getOwner());
        Player player = receiver = duelRequest.getReceiverParty() == null ? duelRequest.getReceiver() : Bukkit.getPlayer(duelRequest.getReceiverParty().getOwner());
        assert (sender != null);
        assert (receiver != null);
        ArenaModel arenaModel = duelRequest.getArena();
        if (arenaModel == null) {
            PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), sender, receiver);
            if (duelRequest.getSignModel() != null) {
                SignCore.INSTANCE.getSignAPI().removeSignFight(duelRequest.getSignModel());
            }
            return;
        }
        if (this.arenaCore.getArenaAPI().isBusyArena(arenaModel) && (arenaModel = this.getFreeArenaName(arenaModel.getDisplayName())) == null) {
            PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), sender, receiver);
            if (duelRequest.getSignModel() != null) {
                SignCore.INSTANCE.getSignAPI().removeSignFight(duelRequest.getSignModel());
            }
            return;
        }
        boolean isLocationError = false;
        if (arenaModel.isFfa()) {
            for (EntityPosition position : arenaModel.getFfaPositions().values()) {
                if (position.getWorld() != null) continue;
                this.plugin.getLogger().info("Мир не найден! Arena: " + arenaModel.getArenaName() + " World: " + position.getWorldName());
                isLocationError = true;
            }
        } else if (arenaModel.getOnePosition() == null || arenaModel.getOnePosition().getWorld() == null || arenaModel.getTwoPosition() == null || arenaModel.getTwoPosition().getWorld() == null) {
            this.plugin.getLogger().info("Arena: " + arenaModel.getArenaName() + " ошибки в локациях. Возможно их не существует");
            isLocationError = true;
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
                --count;
            }
            if (count <= 0) {
                PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), sender, receiver);
                if (duelRequest.getSignModel() != null) {
                    SignCore.INSTANCE.getSignAPI().removeSignFight(duelRequest.getSignModel());
                }
                return;
            }
        }
        DuelFightModel duelFightModel = new DuelFightModel(sender, receiver, duelRequest.getNumGames(), duelRequest.getKitModel(), arenaModel);
        if (duelRequest.getSignModel() != null) {
            duelFightModel.setSignModel(duelRequest.getSignModel());
        }
        PlayerUtil.healPlayers(sender, receiver);
        PlayerUtil.clearEffects(sender, receiver);
        PlayerUtil.setGameModeAndFly(sender, receiver);
        this.fightBucket.addFight(duelFightModel);
        this.arenaCore.getArenaAPI().addBusyArena(duelFightModel.getArenaModel());
        SignAPI signAPI = SignCore.INSTANCE.getSignAPI();
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
            senderPlayers.forEach(signAPI::removePlayerQueueSign);
            receiverPlayers.forEach(signAPI::removePlayerQueueSign);
            DuelStartFightEvent.create(senderParty, receiverParty, duelFightModel).call();
        } else {
            sender.teleport(duelRequest.getArena().getOnePosition().toLocation());
            receiver.teleport(duelRequest.getArena().getTwoPosition().toLocation());
            this.preparationToFight(sender, receiver);
            duelFightModel.getKitModel().giveItemPlayers(sender, receiver);
            DuelStartFightEvent.create(sender, receiver, duelFightModel).call();
        }
        signAPI.removePlayerQueueSign(sender, receiver);
        duelFightModel.setBukkitTask(new DuelScheduler(TimeUtil.parseTime(this.settings.getDurationFight(), TimeUnit.MINUTES), duelFightModel));
        this.duelTeleportBucket.add(new DuelTeleportScheduler(duelFightModel));
        sender.getNearbyEntities(100.0, 100.0, 100.0).stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
    }

    @Override
    public void startFightFour(Player player, Player player2, Player player3, Player player4, DuelRequest duelRequest) {
        ArenaModel arenaModel = duelRequest.getArena();
        if (arenaModel == null) {
            PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), player, player2, player3, player4);
            if (duelRequest.getSignModel() != null) {
                SignCore.INSTANCE.getSignAPI().removeSignFight(duelRequest.getSignModel());
            }
            return;
        }
        if (this.arenaCore.getArenaAPI().isBusyArena(arenaModel) && (arenaModel = this.getFreeArenaName(arenaModel.getDisplayName())) == null) {
            PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), player, player2, player3, player4);
            if (duelRequest.getSignModel() != null) {
                SignCore.INSTANCE.getSignAPI().removeSignFight(duelRequest.getSignModel());
            }
            return;
        }
        boolean isLocationError = false;
        if (arenaModel.isFfa()) {
            for (EntityPosition position : arenaModel.getFfaPositions().values()) {
                if (position.getWorld() != null) continue;
                this.plugin.getLogger().info("Мир не найден! Arena: " + arenaModel.getArenaName() + " World: " + position.getWorldName());
                isLocationError = true;
            }
        } else if (arenaModel.getOnePosition() == null || arenaModel.getOnePosition().getWorld() == null || arenaModel.getTwoPosition() == null || arenaModel.getTwoPosition().getWorld() == null) {
            this.plugin.getLogger().info("Arena: " + arenaModel.getArenaName() + " ошибки в локациях. Возможно их не существует");
            isLocationError = true;
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
                --count;
            }
            if (count == 0) {
                PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), player, player2, player3, player4);
                if (duelRequest.getSignModel() != null) {
                    SignCore.INSTANCE.getSignAPI().removeSignFight(duelRequest.getSignModel());
                }
                return;
            }
        }
        DuelFightModel duelFightModel = new DuelFightModel(player, player3, duelRequest.getNumGames(), duelRequest.getKitModel(), arenaModel);
        if (duelRequest.getSignModel() != null) {
            duelFightModel.setSignModel(duelRequest.getSignModel());
        }
        SignAPI signAPI = SignCore.INSTANCE.getSignAPI();
        signAPI.removePlayerQueueSign(player, player2, player3, player4);
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
        player.getNearbyEntities(100.0, 100.0, 100.0).stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
    }

    @Override
    public void nextRound(DuelFightModel duelFight) {
        if (this.fightBucket.getDuelFights().stream().noneMatch(duelFight::equals)) {
            return;
        }
        Player sender = duelFight.getSender();
        Player receiver = duelFight.getReceiver();
        ArenaModel arenaModel = duelFight.getArenaModel();
        this.preparationToFight(sender, receiver);
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
                this.preparationToFight(player2, player4);
            }
        } else {
            sender.teleport(arenaModel.getOnePosition().toLocation());
            receiver.teleport(arenaModel.getTwoPosition().toLocation());
            PlayerUtil.healPlayers(sender, receiver);
            PlayerUtil.clearEffects(sender, receiver);
            duelFight.getKitModel().giveItemPlayers(sender, receiver);
        }
        duelFight.getSpectates().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(spectator -> spectator.teleport(arenaModel.getSpectatorPosition().toLocation()));
        sender.getNearbyEntities(100.0, 100.0, 100.0).stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        if (arenaModel.isBreaking()) {
            this.arenaCore.getArenaAPI().restoreArena(arenaModel);
        }
        duelFight.getBukkitTask().updateTime(duelFight);
        this.duelTeleportBucket.add(new DuelTeleportScheduler(duelFight));
    }

    @Override
    public void stopFight(DuelFightModel duelFightModel, Player winner, Player loser) {
        this.getTeleportSchedulerFromFight(duelFightModel).ifPresent(teleportScheduler -> {
            this.duelTeleportBucket.remove((DuelTeleportScheduler)teleportScheduler);
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
        ArrayList<Player> winnerParty = new ArrayList<Player>();
        ArrayList<Player> loserParty = new ArrayList<Player>();
        DuelConfig.TitleSettings titleSettings = this.duelCore.getDuelConfig().getTitleSettings();
        if (winner != null) {
            winner.sendTitle(ColorUtil.color(titleSettings.getWin().getText()), "", titleSettings.getWin().getFadeIn(), titleSettings.getWin().getStay(), titleSettings.getWin().getFadeOut());
            winners.append(winner.getName());
        }
        if (loser != null) {
            loser.sendTitle(ColorUtil.color(titleSettings.getLose().getText()), "", titleSettings.getLose().getFadeIn(), titleSettings.getLose().getStay(), titleSettings.getLose().getFadeOut());
            losers.append(loser.getName());
        }
        if (arenaModel.isFfa()) {
            if (duelFightModel.getReceiverParty() != null && duelFightModel.getSenderParty() != null) {
                PartyAPI partyAPI = PartyCore.INSTANCE.getPartyAPI();
                partyAPI.removeFightParty(duelFightModel.getReceiverParty(), duelFightModel.getSenderParty());
                if (winner != null) {
                    for (Player player2 : PlayerUtil.convertListUUID(Objects.requireNonNull(partyAPI.getPartyModelFromPlayer(winner)).getPlayers())) {
                        winnerParty.add(player2);
                        winners.append(" ").append(player2.getName());
                    }
                }
                if (loser != null) {
                    for (Player player2 : PlayerUtil.convertListUUID(Objects.requireNonNull(partyAPI.getPartyModelFromPlayer(loser)).getPlayers())) {
                        loserParty.add(player2);
                        losers.append(" ").append(player2.getName());
                    }
                }
            } else {
                if (winner != null) {
                    if (winner.equals(duelFightModel.getSender())) {
                        winners.append(" ").append(duelFightModel.getPlayer2().getName());
                        duelFightModel.getPlayer2().sendTitle(ColorUtil.color(titleSettings.getWin().getText()), "", titleSettings.getWin().getFadeIn(), titleSettings.getWin().getStay(), titleSettings.getWin().getFadeOut());
                    } else {
                        winners.append(" ").append(duelFightModel.getPlayer4().getName());
                        duelFightModel.getPlayer4().sendTitle(ColorUtil.color(titleSettings.getWin().getText()), "", titleSettings.getWin().getFadeIn(), titleSettings.getWin().getStay(), titleSettings.getWin().getFadeOut());
                    }
                }
                if (loser != null) {
                    if (loser.equals(duelFightModel.getSender())) {
                        losers.append(" ").append(duelFightModel.getPlayer2().getName());
                        duelFightModel.getPlayer2().sendTitle(ColorUtil.color(titleSettings.getLose().getText()), "", titleSettings.getLose().getFadeIn(), titleSettings.getLose().getStay(), titleSettings.getLose().getFadeOut());
                    } else {
                        losers.append(" ").append(duelFightModel.getPlayer4().getName());
                        duelFightModel.getPlayer4().sendTitle(ColorUtil.color(titleSettings.getLose().getText()), "", titleSettings.getLose().getFadeIn(), titleSettings.getLose().getStay(), titleSettings.getLose().getFadeOut());
                    }
                }
            }
        }
        this.arenaCore.getArenaAPI().removeBusyArena(arenaModel);
        Player receiver = duelFightModel.getReceiver();
        Player sender = duelFightModel.getSender();
        this.messageConfiguration.getMessages("endFight").stream().map(str -> str.replace("(playersHealth)", winner == null ? "" : this.getHealthPlayers(duelFightModel, loser))).map(str -> str.replace("(winner)", winner == null ? this.messageConfiguration.getMessage("replaceWinnerNull") : winners.toString())).map(str -> str.replace("(loser)", loser == null ? this.messageConfiguration.getMessage("replaceLoserNull") : losers.toString())).map(str -> str.replace("(kit)", ColorUtil.color(duelFightModel.getKitModel().getDisplayName()))).map(str -> str.replace("(numGames)", String.valueOf(duelFightModel.getNumGames()))).forEach(text -> {
            PlayerUtil.sendMessage(text, receiver, sender);
            if (arenaModel.isFfa()) {
                if (duelFightModel.getReceiverParty() != null && duelFightModel.getSenderParty() != null) {
                    winnerParty.forEach(player -> player.sendMessage((String)text));
                    loserParty.forEach(player -> player.sendMessage((String)text));
                } else {
                    PlayerUtil.sendMessage(text, duelFightModel.getPlayer2(), duelFightModel.getPlayer4());
                }
            }
        });
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
                    player.sendTitle(ColorUtil.color(titleSettings.getWin().getText()), "", titleSettings.getWin().getFadeIn(), titleSettings.getWin().getStay(), titleSettings.getWin().getFadeOut());
                    PartyCore.INSTANCE.getPartyAPI().giveStartItems((Player)player);
                });
                loserParty.forEach(player -> {
                    player.teleport(location);
                    PlayerUtil.clearEffects(player);
                    PlayerUtil.healPlayers(player);
                    if (player.getGameMode() != GameMode.SURVIVAL) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                    player.sendTitle(ColorUtil.color(titleSettings.getLose().getText()), "", titleSettings.getLose().getFadeIn(), titleSettings.getLose().getStay(), titleSettings.getLose().getFadeOut());
                    PartyCore.INSTANCE.getPartyAPI().giveStartItems((Player)player);
                });
            } else {
                PartyAPI partyAPI;
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
                if ((partyAPI = PartyCore.INSTANCE.getPartyAPI()).isPartyPlayer(player2)) {
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
                spectators.forEach(player -> this.removeSpectate((Player)player, duelFightModel, false));
            }
            catch (ConcurrentModificationException ignored) {
                Iterator<UUID> spectatorsUUID = duelFightModel.getSpectates().iterator();
                while (spectatorsUUID.hasNext()) {
                    Player player3 = Bukkit.getPlayer(spectatorsUUID.next());
                    if (player3 == null) continue;
                    this.removeSpectate(player3, duelFightModel, false);
                }
            }
        }
        if (arenaModel.isBreaking()) {
            this.arenaCore.getArenaAPI().restoreArena(arenaModel);
        }
        DuelStopFightEvent.create(duelFightModel.getSender(), duelFightModel.getReceiver(), winner, loser, duelFightModel).call();
    }

    private String getHealthPlayers(DuelFightModel duelFightModel, Player loser) {
        StringBuilder sb = new StringBuilder();
        Player receiver = duelFightModel.getReceiver();
        Player sender = duelFightModel.getSender();
        if (!duelFightModel.getArenaModel().isFfa()) {
            return sender.equals(loser) ? sb.append(receiver.getName()).append(" ").append(receiver.getHealth()).toString() : sb.append(sender.getName()).append(" ").append(sender.getHealth()).toString();
        }
        Player player2 = duelFightModel.getPlayer2();
        Player player4 = duelFightModel.getPlayer4();
        if (player2 != null && player4 != null) {
            if (player2.equals(loser) || sender.equals(loser)) {
                if (receiver.getGameMode() != GameMode.SPECTATOR) {
                    sb.append(receiver.getName()).append(" ").append(receiver.getHealth());
                }
                if (player4.getGameMode() != GameMode.SPECTATOR) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(player4.getName()).append(" ").append(player4.getHealth());
                }
                return sb.toString();
            }
            if (duelFightModel.getSender().getGameMode() != GameMode.SPECTATOR) {
                sb.append(sender.getName()).append(" ").append(sender.getHealth());
            }
            if (player2.getGameMode() != GameMode.SPECTATOR) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(player2.getName()).append(" ").append(player2.getHealth());
            }
            return sb.toString();
        }
        if (duelFightModel.getReceiverParty() != null && duelFightModel.getSenderParty() != null) {
            PartyModel winnerParty = duelFightModel.getReceiverParty().getOwner().equals(loser.getUniqueId()) || duelFightModel.getReceiverParty().getPlayers().contains(loser.getUniqueId()) ? duelFightModel.getSenderParty() : duelFightModel.getReceiverParty();
            Player winnerOwner = Bukkit.getPlayer(winnerParty.getOwner());
            if (winnerOwner != null && winnerOwner.getGameMode() != GameMode.SPECTATOR) {
                sb.append(winnerOwner.getName()).append(" ").append(winnerOwner.getHealth());
            }
            PlayerUtil.convertListUUID(winnerParty.getPlayers()).stream().filter(player -> player.getGameMode() != GameMode.SPECTATOR).forEach(player -> {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(player.getName()).append(" ").append(player.getHealth());
            });
            return sb.toString();
        }
        return "";
    }

    @Override
    @Nullable
    public ArenaModel getFreeArena() {
        List arenaModels = this.arenaCore.getArenas().getArenas().stream().filter(arenaModel -> !arenaModel.isFfa()).filter(arenaModel -> !arenaModel.isCustomKits()).filter(arenaModel -> !this.arenaCore.getArenaBusyBucket().getArenas().contains(arenaModel)).collect(Collectors.toList());
        return arenaModels.isEmpty() ? null : (ArenaModel)arenaModels.get(ThreadLocalRandom.current().nextInt(arenaModels.size()));
    }

    @Override
    @Nullable
    public ArenaModel getFreeArenaName(String name) {
        for (ArenaModel arena : this.arenaCore.getArenaAPI().getArenasFromName(name)) {
            if (this.arenaCore.getArenaBusyBucket().getArenas().isEmpty()) {
                return arena;
            }
            for (ArenaModel bussyArena : this.arenaCore.getArenaBusyBucket().getArenas()) {
                if (arena.equals(bussyArena)) continue;
                return arena;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public ArenaModel getFreeArenaFFA() {
        return this.arenaCore.getArenas().getArenas().stream().filter(arenaModel -> !this.arenaCore.getArenaBusyBucket().getArenas().contains(arenaModel)).filter(ArenaModel::isFfa).findFirst().orElse(null);
    }

    @Override
    @Nullable
    public List<DuelRequest> getRequestsFromReceiver(Player receiver) {
        return this.requestsBucket.getRequests().stream().filter(duelRequest -> duelRequest.getReceiver().getUniqueId().equals(receiver.getUniqueId())).collect(Collectors.toList());
    }

    @Override
    public Player getWinnerFromFight(DuelFightModel duelFightModel, Player loser) {
        return duelFightModel.getReceiver().getUniqueId().equals(loser.getUniqueId()) ? duelFightModel.getSender() : duelFightModel.getReceiver();
    }

    @Override
    public Player getLoserFromFight(DuelFightModel duelFightModel, Player winner) {
        return duelFightModel.getReceiver().getUniqueId().equals(winner.getUniqueId()) ? duelFightModel.getSender() : duelFightModel.getReceiver();
    }

    @Override
    public Player getOpponentFromFight(DuelFightModel duelFightModel, Player player) {
        return duelFightModel.getReceiver().getUniqueId().equals(player.getUniqueId()) ? duelFightModel.getSender() : duelFightModel.getReceiver();
    }

    @Override
    @Nullable
    public Player getOpponentFromFight(Player player) {
        return this.getOpponentFromFight(Objects.requireNonNull(this.getFightModelFromPlayer(player)), player);
    }

    @Override
    @Nullable
    public DuelRequest getRequestFromSender(Player sender, Player receiver) {
        return this.requestsBucket.getRequests().stream().filter(request -> request.getSender().getUniqueId().equals(sender.getUniqueId())).filter(request -> request.getReceiver().getUniqueId().equals(receiver.getUniqueId())).findFirst().orElse(null);
    }

    @Override
    public void saveKitServer(Player player, String kitName) {
        HashMap<Integer, ItemBuilder> items = new HashMap<Integer, ItemBuilder>();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getItemInMainHand();
        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack inventoryItem = inventory.getItem(i);
            if (inventoryItem == null || inventoryItem.getType().isAir()) continue;
            if (!itemStack.getType().isAir()) {
                itemStack = inventoryItem;
            }
            items.put(i, ItemBuilder.builder().fromItemStack(inventoryItem));
        }
        KitModel kitModel = KitModel.create(kitName, this.getFreeSlotKit(), new ArrayList<String>(), itemStack.getType().isAir() ? Material.PAPER : itemStack.getType(), items);
        this.kitConfig.getKits().add(kitModel);
        this.kitConfig.save();
        player.sendMessage("Вы успешно сохранили кит " + kitName);
    }

    @Override
    public boolean isKitNameContains(String kitName) {
        return this.kitConfig.getKits().stream().map(KitModel::getDisplayName).anyMatch(name -> name.equalsIgnoreCase(kitName));
    }

    @Override
    public int getFreeSlotKit() {
        for (int i = 0; i < this.duelCore.getDuelConfig().getChoiceKitMenu().getRequestKit().getSize(); ++i) {
            for (ItemBuilder builder : this.duelCore.getDuelConfig().getChoiceKitMenu().getRequestKit().getKits().keySet()) {
                if (i == builder.getSlot()) continue;
                return i;
            }
        }
        return -1;
    }

    @Override
    public Location getRandomSpawn() {
        return this.settings.getSpawns().get(ThreadLocalRandom.current().nextInt(this.settings.getSpawns().size())).toLocation();
    }

    @Override
    public void giveStartItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        if (this.settings.isItemOpenCustomKit()) {
            player.getInventory().setItem(this.settings.getCreateCustomKit().getSlot(), this.settings.getCreateCustomKit().build());
        }
        player.updateInventory();
    }

    @Override
    public KitModel getRandomKit() {
        ArrayList<KitModel> kit = new ArrayList<KitModel>(this.duelCore.getDuelConfig().getChoiceKitMenu().getRequestKit().getKits().values());
        return (KitModel)kit.get(ThreadLocalRandom.current().nextInt(kit.size()));
    }

    @Override
    public void addNoMove(Player player) {
        if (!this.isNoMovePlayer(player)) {
            this.duelMoveBucket.add(player);
        }
    }

    @Override
    public void removeNoMove(Player player) {
        if (this.isNoMovePlayer(player)) {
            this.duelMoveBucket.remove(player);
        }
    }

    @Override
    public boolean isNoMovePlayer(Player player) {
        return this.duelMoveBucket.contains(player);
    }

    @Override
    public void addSpectate(Player player, DuelFightModel duelFightModel) {
        duelFightModel.getSpectates().add(player.getUniqueId());
        player.teleport(duelFightModel.getArenaModel().getSpectatorPosition().toLocation());
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        PlayerUtil.sendMessage(this.messageConfiguration.getMessage("startSpectate").replace("(player)", player.getName()), duelFightModel.getReceiver(), duelFightModel.getSender());
    }

    @Override
    public void removeSpectate(Player player, DuelFightModel duelFightModel, boolean fighting) {
        duelFightModel.getSpectates().remove(player.getUniqueId());
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(this.getRandomSpawn());
        if (fighting) {
            PlayerUtil.sendMessage(this.messageConfiguration.getMessage("stopSpectate").replace("(player)", player.getName()), duelFightModel.getSender(), duelFightModel.getReceiver());
        }
    }

    @Override
    public boolean isSpectate(Player player) {
        return this.getDuelFightModelFromSpectator(player) != null;
    }

    @Override
    @Nullable
    public DuelFightModel getDuelFightModelFromSpectator(Player player) {
        for (DuelFightModel fightModel : this.fightBucket.getDuelFights()) {
            for (UUID spectator : fightModel.getSpectates()) {
                if (!spectator.equals(player.getUniqueId())) continue;
                return fightModel;
            }
        }
        return null;
    }

    @Override
    public void preparationToFight(List<Player> players) {
        players.forEach(player -> {
            player.setFlying(false);
            if (this.isSpectate((Player)player)) {
                this.removeSpectate((Player)player, Objects.requireNonNull(this.getDuelFightModelFromSpectator((Player)player)), true);
            }
            if (player.getGameMode() != GameMode.SURVIVAL) {
                player.setGameMode(GameMode.SURVIVAL);
            }
            player.setFireTicks(0);
            PlayerUtil.clearEffects(player);
            PlayerUtil.healPlayers(player);
        });
    }

    @Override
    public void preparationToFight(Player ... players) {
        this.preparationToFight(Arrays.asList(players));
    }

    @Override
    public void preparationToFight(PartyModel senderParty, PartyModel receiverParty) {
        this.preparationToFight(PlayerUtil.convertListUUID(senderParty.getPlayers()));
        this.preparationToFight(PlayerUtil.convertListUUID(receiverParty.getPlayers()));
        this.preparationToFight(Bukkit.getPlayer(senderParty.getOwner()), Bukkit.getPlayer(receiverParty.getOwner()));
    }

    @Override
    public Optional<DuelTeleportScheduler> getTeleportSchedulerFromFight(DuelFightModel duelFightModel) {
        return this.duelTeleportBucket.getTeleportSchedulerFromFight(duelFightModel);
    }
}

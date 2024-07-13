package ru.merkii.rduels.core.sign.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.sign.SignCore;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.core.sign.event.SignClickEvent;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.core.sign.model.SignQueueModel;
import ru.merkii.rduels.core.sign.util.SignUtil;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.KitModel;
import ru.merkii.rduels.util.ColorUtil;
import ru.merkii.rduels.util.PlayerUtil;
import java.util.Objects;
import java.util.Optional;

public class SignListener implements Listener {

    private final SignAPI signAPI = SignCore.INSTANCE.getSignAPI();
    private final PartyAPI partyAPI = PartyCore.INSTANCE.getPartyAPI();
    private final MessageConfiguration messageConfiguration = RDuels.getInstance().getPluginMessage();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || !block.getType().name().contains("SIGN") || !(block.getState() instanceof Sign)) {
            return;
        }
        BlockPosition blockPosition = new BlockPosition(block);
        if (this.signAPI.isContainsSignPosition(blockPosition)) {
            Optional.ofNullable(this.signAPI.getModelInBlockPosition(blockPosition)).ifPresent(signModel -> SignClickEvent.create(event.getPlayer(), block, (Sign)((Object)block.getState()), signModel, event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK, event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK).call());
        }
    }

    @EventHandler
    public void onSignClick(SignClickEvent event) {
        SignModel signModel = event.getSignModel();
        Player player = event.getPlayer();
        Sign sign = event.getSign();
        event.setCancelled(true);
        PartyModel partyModel = this.partyAPI.getPartyModelFromPlayer(player);
        if (this.signAPI.isFightSign(signModel)) {
            return;
        }
        if (this.signAPI.isClickedSignQueuePlayer(player, signModel)) {
            SignQueueModel signQueueModel = this.signAPI.getQueueInSignModel(signModel);
            if (signQueueModel == null) {
                return;
            }
            if (signModel.getDuelType().getSize() == 4) {
                if (partyModel != null) {
                    if (!partyModel.getOwner().equals(player.getUniqueId())) {
                        return;
                    }
                    this.setPlayerQueue(signQueueModel, player, null);
                    this.setPlayerQueue(signQueueModel, Objects.requireNonNull(Bukkit.getPlayer(partyModel.getPlayers().get(0))), null);
                    this.signAPI.setSignWait(sign, this.getSizeQueue(signQueueModel), signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
                    return;
                }
                this.setPlayerQueue(signQueueModel, player, null);
                int size = this.getSizeQueue(signQueueModel);
                this.signAPI.setSignWait(sign, size, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
                return;
            }
            this.signAPI.removeQueueSign(signQueueModel);
            if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                signModel.setKitModel(null);
            }
            this.signAPI.setSignWait(sign, 0, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
            return;
        }
        if (this.signAPI.isQueuePlayer(player)) {
            player.sendMessage(this.messageConfiguration.getMessage("queueAlready"));
            return;
        }
        SignQueueModel signQueueModel = this.signAPI.getQueueInSignModel(signModel);
        if (signQueueModel == null) {
            if (partyModel != null) {
                this.onParty(partyModel, player, signModel, sign);
                return;
            }
            if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                signModel.setKitModel(CustomKitCore.INSTANCE.getCustomKitAPI().getKitModel(player));
                if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                    signModel.setKitModel(null);
                    player.sendMessage(RDuels.getInstance().getPluginMessage().getMessage("signNoStart"));
                    return;
                }
            }
            this.signAPI.addQueueSign(SignQueueModel.create(signModel, player));
            this.signAPI.setSignWait(sign, 1, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
            player.sendMessage(this.messageConfiguration.getMessage("signStartQueue").replace("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())));
            return;
        }
        if (signModel.getDuelType().getSize() == 2) {
            if (partyModel != null && !partyModel.getPlayers().isEmpty()) {
                return;
            }
            this.startFight(signModel, sign, signQueueModel.getSender(), player, false);
            return;
        }
        if (partyModel != null) {
            if (this.getSizeQueue(signQueueModel) == 3) {
                return;
            }
            if (partyModel.getPlayers().size() != 1) {
                return;
            }
            if (signQueueModel.getSender() == null && signQueueModel.getSenderHelper() == null) {
                signQueueModel.setSender(player);
                signQueueModel.setSenderHelper(Bukkit.getPlayer(partyModel.getPlayers().get(0)));
                if (signQueueModel.getReceiver() != null && signQueueModel.getReceiverHelper() != null) {
                    this.startFightFour(sign, signQueueModel, signModel);
                    return;
                }
                this.signAPI.setSignWait(sign, this.getSizeQueue(signQueueModel), signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
                player.sendMessage(this.messageConfiguration.getMessage("signStartQueue").replace("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())));
                return;
            }
            if (signQueueModel.getReceiver() == null && signQueueModel.getReceiverHelper() == null) {
                signQueueModel.setReceiver(player);
                signQueueModel.setReceiverHelper(Bukkit.getPlayer(partyModel.getPlayers().get(0)));
                if (signQueueModel.getSender() != null && signQueueModel.getSenderHelper() != null) {
                    this.startFightFour(sign, signQueueModel, signModel);
                    return;
                }
                this.signAPI.setSignWait(sign, this.getSizeQueue(signQueueModel), signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
                player.sendMessage(this.messageConfiguration.getMessage("signStartQueue").replace("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())));
                return;
            }
            return;
        }
        if (signQueueModel.getSender() == null) {
            if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                signModel.setKitModel(CustomKitCore.INSTANCE.getCustomKitAPI().getKitModel(player));
                if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                    signModel.setKitModel(null);
                    player.sendMessage(RDuels.getInstance().getPluginMessage().getMessage("signNoStart"));
                    return;
                }
            }
            signQueueModel.setSender(player);
            int size = this.getSizeQueue(signQueueModel);
            if (size == 4) {
                this.startFightFour(sign, signQueueModel, signModel);
                return;
            }
            this.signAPI.setSignWait(sign, size, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
            player.sendMessage(this.messageConfiguration.getMessage("signStartQueue").replace("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())));
            return;
        }
        if (signQueueModel.getSenderHelper() == null) {
            signQueueModel.setSenderHelper(player);
            int size = this.getSizeQueue(signQueueModel);
            if (size == 4) {
                this.startFightFour(sign, signQueueModel, signModel);
                return;
            }
            this.signAPI.setSignWait(sign, size, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
            return;
        }
        if (signQueueModel.getReceiver() == null) {
            signQueueModel.setReceiver(player);
            int size = this.getSizeQueue(signQueueModel);
            if (size == 4) {
                this.startFightFour(sign, signQueueModel, signModel);
                return;
            }
            this.signAPI.setSignWait(sign, size, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
            return;
        }
        signQueueModel.setReceiverHelper(player);
        this.startFightFour(sign, signQueueModel, signModel);
    }

    private void onParty(PartyModel partyModel, Player player, SignModel signModel, Sign sign) {
        if (!partyModel.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNoOwner"));
            return;
        }
        if (signModel.getDuelType().getSize() == 2) {
            if (partyModel.getPlayers().size() != 1) {
                return;
            }
            if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                signModel.setKitModel(CustomKitCore.INSTANCE.getCustomKitAPI().getKitModel(player));
                if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                    signModel.setKitModel(null);
                    player.sendMessage(RDuels.getInstance().getPluginMessage().getMessage("signNoStart"));
                    return;
                }
            }
            this.startFight(signModel, sign, player, Bukkit.getPlayer(partyModel.getPlayers().get(0)), false);
            return;
        }
        if (signModel.getDuelType().getSize() == 4) {
            if (partyModel.getPlayers().size() == 1) {
                if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                    signModel.setKitModel(CustomKitCore.INSTANCE.getCustomKitAPI().getKitModel(player));
                    if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                        signModel.setKitModel(null);
                        player.sendMessage(RDuels.getInstance().getPluginMessage().getMessage("signNoStart"));
                        return;
                    }
                }
                this.signAPI.addQueueSign(SignQueueModel.builder().signModel(signModel).sender(player).senderHelper(Bukkit.getPlayer(partyModel.getPlayers().get(0))).build());
                this.signAPI.setSignWait(sign, 2, 4, signModel.getDuelKit(), signModel.getKitModel().getDisplayName());
                player.sendMessage(this.messageConfiguration.getMessage("signStartQueue").replace("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())));
                return;
            }
            if (partyModel.getPlayers().size() != 3) {
                return;
            }
            if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                signModel.setKitModel(CustomKitCore.INSTANCE.getCustomKitAPI().getKitModel(player));
                if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                    signModel.setKitModel(null);
                    player.sendMessage(RDuels.getInstance().getPluginMessage().getMessage("signNoStart"));
                    return;
                }
            }
            SignQueueModel signQueue = SignQueueModel.builder().signModel(signModel).sender(player).senderHelper(Bukkit.getPlayer(partyModel.getPlayers().get(0))).receiver(Bukkit.getPlayer(partyModel.getPlayers().get(1))).receiverHelper(Bukkit.getPlayer(partyModel.getPlayers().get(2))).build();
            this.startFightFour(sign, signQueue, signModel);
            return;
        }
    }

    private void startFightFour(Sign sign, SignQueueModel signQueueModel, SignModel signModel) {
        DuelRequest duelRequest = DuelRequest.create(signQueueModel.getSender(), signQueueModel.getReceiver());
        duelRequest.setKitModel(signModel.getKitModel());
        duelRequest.setNumGames(1);
        duelRequest.setDuelKit(signModel.getDuelKit());
        duelRequest.setKitName(signModel.getKitModel().getDisplayName());
        duelRequest.setSignModel(signModel);
        duelRequest.setArena(DuelCore.INSTANCE.getDuelAPI().getFreeArenaFFA());
        this.signAPI.setSignActive(sign, signQueueModel.getSender(), signQueueModel.getReceiver(), signModel.getDuelKit());
        this.signAPI.addSignFight(signModel);
        DuelCore.INSTANCE.getDuelAPI().startFightFour(signQueueModel.getSender(), signQueueModel.getSenderHelper(), signQueueModel.getReceiver(), signQueueModel.getReceiverHelper(), duelRequest);
    }

    private int getSizeQueue(SignQueueModel signQueueModel) {
        int size = 0;
        if (signQueueModel.getSender() != null) {
            ++size;
        }
        if (signQueueModel.getSenderHelper() != null) {
            ++size;
        }
        if (signQueueModel.getReceiver() != null) {
            ++size;
        }
        if (signQueueModel.getReceiverHelper() != null) {
            ++size;
        }
        return size;
    }

    public void setPlayerQueue(SignQueueModel signQueueModel, Player player, Player set) {
        if (signQueueModel.getSender() != null && signQueueModel.getSender().getUniqueId().equals(player.getUniqueId())) {
            signQueueModel.setSender(set);
            if (signQueueModel.getSignModel().getDuelKit() == DuelKitType.CUSTOM) {
                signQueueModel.getSignModel().setKitModel(null);
            }
        } else if (signQueueModel.getReceiver() != null && signQueueModel.getReceiver().getUniqueId().equals(player.getUniqueId())) {
            signQueueModel.setReceiver(set);
        } else if (signQueueModel.getReceiverHelper() != null && signQueueModel.getReceiverHelper().getUniqueId().equals(player.getUniqueId())) {
            signQueueModel.setReceiverHelper(set);
        } else {
            signQueueModel.setSenderHelper(set);
        }
    }

    public void startFight(SignModel signModel, Sign sign, Player sender, Player receiver, boolean ffa) {
        DuelRequest duelRequest = DuelRequest.create(sender, receiver);
        duelRequest.setDuelKit(signModel.getDuelKit());
        duelRequest.setNumGames(1);
        duelRequest.setKitModel(signModel.getKitModel());
        duelRequest.setKitName(signModel.getKitModel().getDisplayName());
        duelRequest.setSignModel(signModel);
        ArenaModel arenaModel = null;
        if (signModel.getKitModel().isBindingArena()) {
            if (signModel.getKitModel() != null && !ArenaCore.INSTANCE.getArenaAPI().getArenaFromKit(signModel.getKitModel()).isPresent()) {
                SignUtil.clearSignsLines(sign);
                this.signAPI.setSignWait(sign, 0, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
                this.signAPI.removePlayerQueueSign(sender, receiver);
                PlayerUtil.sendMessage(this.messageConfiguration.getMessage("duelArenasFull"), sender, receiver);
                return;
            }
        } else {
            arenaModel = DuelCore.INSTANCE.getDuelAPI().getFreeArena();
        }
        duelRequest.setArena(arenaModel);
        this.signAPI.setSignActive(sign, sender, receiver, signModel.getDuelKit());
        this.signAPI.addSignFight(signModel);
        DuelCore.INSTANCE.getDuelAPI().startFight(duelRequest);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.onLeave(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        this.onLeave(event.getPlayer());
    }

    private void onLeave(Player player) {
        if (!this.signAPI.isQueuePlayer(player)) {
            return;
        }
        this.signAPI.getQueueFromPlayer(player).ifPresent(signQueueModel -> {
            Player player2;
            PartyModel partyModel;
            this.setPlayerQueue(signQueueModel, player, null);
            if (this.partyAPI.isPartyPlayer(player) && (partyModel = this.partyAPI.getPartyModelFromPlayer(player)) != null && partyModel.getOwner().equals(player.getUniqueId()) && !partyModel.getPlayers().isEmpty() && (player2 = Bukkit.getPlayer(partyModel.getPlayers().get(0))) != null) {
                this.setPlayerQueue(signQueueModel, player2, null);
            }
            Sign sign = (Sign) player.getWorld().getBlockAt(signQueueModel.getSignModel().getBlockPosition().toLocation()).getState();
            KitModel kitModel = signQueueModel.getSignModel().getKitModel();
            this.signAPI.setSignWait(sign, this.getSizeQueue(signQueueModel), signQueueModel.getSignModel().getDuelType().getSize(), signQueueModel.getSignModel().getDuelKit(), kitModel != null ? this.messageConfiguration.getMessage("signServerReplacer").replace("(kit)", kitModel.getDisplayName()) : this.messageConfiguration.getMessage("signCustomReplacer"));
        });
    }

}

package ru.merkii.rduels.core.sign.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.core.sign.event.SignClickEvent;
import ru.merkii.rduels.core.sign.model.SignQueueModel;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.core.sign.util.SignUtil;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.KitModel;
import ru.merkii.rduels.util.ColorUtil;

import java.util.Arrays;
import java.util.Optional;

@Singleton
public class SignListener implements Listener {

    private final SignAPI signAPI;
    private final PartyAPI partyAPI;
    private final MessageConfig messageConfig;
    private final DuelAPI duelAPI;
    private final ArenaAPI arenaAPI;
    private final CustomKitAPI customKitAPI;

    @Inject
    public SignListener(SignAPI signAPI, PartyAPI partyAPI, MessageConfig messageConfig, DuelAPI duelAPI, ArenaAPI arenaAPI, CustomKitAPI customKitAPI) {
        this.signAPI = signAPI;
        this.partyAPI = partyAPI;
        this.messageConfig = messageConfig;
        this.duelAPI = duelAPI;
        this.arenaAPI = arenaAPI;
        this.customKitAPI = customKitAPI;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || !block.getType().name().contains("SIGN") || !(block.getState() instanceof Sign)) {
            return;
        }
        BlockPosition blockPosition = new BlockPosition(block);
        if (this.signAPI.isContainsSignPosition(blockPosition)) {
            event.setCancelled(true);
            Optional.ofNullable(this.signAPI.getModelInBlockPosition(blockPosition)).ifPresent(signModel -> SignClickEvent.create(event.getPlayer(), block, (Sign) block.getState(), signModel, event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK, event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK).call());
        }
    }

    @EventHandler
    public void onSignClick(SignClickEvent event) {
        SignModel signModel = event.getSignModel();
        Player bukkitPlayer = event.getPlayer();
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        Sign sign = event.getSign();
        PartyModel partyModel = this.partyAPI.getPartyModelFromPlayer(player);
        if (this.signAPI.isFightSign(signModel)) {
            return;
        }
        if (this.signAPI.isClickedSignQueuePlayer(player, signModel)) {
            this.handleQueueSign(signModel, partyModel, sign, player);
            return;
        }
        if (this.signAPI.isQueuePlayer(player)) {
            return;
        }
        SignQueueModel signQueueModel = this.signAPI.getQueueInSignModel(signModel);
        if (signQueueModel == null) {
            handleEmptyQueueSign(partyModel, signModel, sign, player);
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
            handleParty(signQueueModel, partyModel, signModel, sign, player);
            return;
        }
        handleFourSign(signQueueModel, signModel, sign, player);
    }

    private void handleFourSign(SignQueueModel signQueueModel, SignModel signModel, Sign sign, DuelPlayer player) {
        if (signQueueModel.getSender() == null) {
            if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                signModel.setKitModel(customKitAPI.getKitModel(player));
                if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                    signModel.setKitModel(null);
                    messageConfig.sendTo(player, "sign-no-start");
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
            messageConfig.sendTo(player, Placeholder.wrapped("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())), "sign-start-queue");
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

    private void handleParty(SignQueueModel signQueueModel, PartyModel partyModel, SignModel signModel, Sign sign, DuelPlayer player) {
        if (this.getSizeQueue(signQueueModel) == 3) {
            return;
        }
        if (partyModel.getPlayers().size() != 1) {
            return;
        }
        if (signQueueModel.getSender() == null && signQueueModel.getSenderHelper() == null) {
            signQueueModel.setSender(player);
            signQueueModel.setSenderHelper(BukkitAdapter.getPlayer(partyModel.getPlayers().getFirst()));
            if (signQueueModel.getReceiver() != null && signQueueModel.getReceiverHelper() != null) {
                this.startFightFour(sign, signQueueModel, signModel);
                return;
            }
            this.signAPI.setSignWait(sign, this.getSizeQueue(signQueueModel), signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
            messageConfig.sendTo(player, Placeholder.wrapped("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())), "sign-start-queue");
            return;
        }
        if (signQueueModel.getReceiver() == null && signQueueModel.getReceiverHelper() == null) {
            signQueueModel.setReceiver(player);
            signQueueModel.setReceiverHelper(BukkitAdapter.getPlayer(partyModel.getPlayers().getFirst()));
            if (signQueueModel.getSender() != null && signQueueModel.getSenderHelper() != null) {
                this.startFightFour(sign, signQueueModel, signModel);
                return;
            }
            this.signAPI.setSignWait(sign, this.getSizeQueue(signQueueModel), signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
            messageConfig.sendTo(player, Placeholder.wrapped("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())), "sign-start-queue");
        }
    }

    private void handleEmptyQueueSign(PartyModel partyModel, SignModel signModel, Sign sign, DuelPlayer player) {
        if (partyModel != null) {
            this.onParty(partyModel, player, signModel, sign);
            return;
        }
        if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
            signModel.setKitModel(customKitAPI.getKitModel(player));
            if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                signModel.setKitModel(null);
                messageConfig.sendTo(player, "sign-no-start");
                return;
            }
        }
        this.signAPI.addQueueSign(SignQueueModel.create(signModel, player));
        this.signAPI.setSignWait(sign, 1, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel().getDisplayName());
        messageConfig.sendTo(player, Placeholder.wrapped("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())), "sign-start-queue");
    }

    private void handleQueueSign(SignModel signModel, PartyModel partyModel, Sign sign, DuelPlayer player) {
        SignQueueModel signQueueModel = this.signAPI.getQueueInSignModel(signModel);
        if (signQueueModel == null) {
            return;
        }
        if (signModel.getDuelType().getSize() == 4) {
            if (partyModel != null) {
                if (!partyModel.getOwner().equals(player.getUUID())) {
                    return;
                }
                this.setPlayerQueue(signQueueModel, player, null);
                this.setPlayerQueue(signQueueModel, BukkitAdapter.getPlayer(partyModel.getPlayers().getFirst()), null);
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
        this.signAPI.setSignWait(sign, 0, signModel.getDuelType().getSize(), signModel.getDuelKit(), "");
    }

    private void onParty(PartyModel partyModel, DuelPlayer player, SignModel signModel, Sign sign) {
        if (!partyModel.getOwner().equals(player.getUUID())) {
            messageConfig.sendTo(player, "party-no-owner");
            return;
        }
        if (signModel.getDuelType().getSize() == 2) {
            if (partyModel.getPlayers().size() != 1) {
                return;
            }
            if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                signModel.setKitModel(customKitAPI.getKitModel(player));
                if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                    signModel.setKitModel(null);
                    messageConfig.sendTo(player, "sign-no-start");
                    return;
                }
            }
            this.startFight(signModel, sign, player, BukkitAdapter.getPlayer(partyModel.getPlayers().getFirst()), false);
            return;
        }
        if (signModel.getDuelType().getSize() == 4) {
            if (partyModel.getPlayers().size() == 1) {
                if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                    signModel.setKitModel(customKitAPI.getKitModel(player));
                    if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                        signModel.setKitModel(null);
                        messageConfig.sendTo(player, "sign-no-start");
                        return;
                    }
                }
                this.signAPI.addQueueSign(SignQueueModel.builder().signModel(signModel).sender(player).senderHelper(BukkitAdapter.getPlayer(partyModel.getPlayers().getFirst())).build());
                this.signAPI.setSignWait(sign, 2, 4, signModel.getDuelKit(), signModel.getKitModel().getDisplayName());
                messageConfig.sendTo(player, Placeholder.wrapped("(kit)", ColorUtil.color(signModel.getKitModel().getDisplayName())), "sign-start-queue");
                return;
            }
            if (partyModel.getPlayers().size() != 3) {
                return;
            }
            if (signModel.getDuelKit() == DuelKitType.CUSTOM) {
                signModel.setKitModel(customKitAPI.getKitModel(player));
                if (signModel.getKitModel().getDisplayName().equalsIgnoreCase("null")) {
                    signModel.setKitModel(null);
                    messageConfig.sendTo(player, "sign-no-start");
                    return;
                }
            }
            SignQueueModel signQueue = SignQueueModel.builder()
                    .signModel(signModel)
                    .sender(player)
                    .senderHelper(BukkitAdapter.getPlayer(partyModel.getPlayers().getFirst()))
                    .receiver(BukkitAdapter.getPlayer(partyModel.getPlayers().get(1)))
                    .receiverHelper(BukkitAdapter.getPlayer(partyModel.getPlayers().get(2)))
                    .build();
            this.startFightFour(sign, signQueue, signModel);
        }
    }

    private void startFightFour(Sign sign, SignQueueModel signQueueModel, SignModel signModel) {
        DuelRequest duelRequest = DuelRequest.create(signQueueModel.getSender(), signQueueModel.getReceiver());
        duelRequest.setKitModel(signModel.getKitModel());
        duelRequest.setNumGames(1);
        duelRequest.setDuelKit(signModel.getDuelKit());
        duelRequest.setKitName(signModel.getKitModel().getDisplayName());
        duelRequest.setSignModel(signModel);
        ArenaModel arenaModel = null;
        if (signModel.getKitModel().isBindingArena()) {
            if (arenaAPI.getArenaFromKit(signModel.getKitModel()).isEmpty()) {
                SignUtil.clearSignsLines(sign);
                this.signAPI.setSignWait(sign, 0, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
                this.signAPI.removePlayerQueueSign(signQueueModel.getSender(), signQueueModel.getSenderHelper(), signQueueModel.getReceiver(), signQueueModel.getReceiverHelper());
                messageConfig.sendTo(Arrays.asList(signQueueModel.getSender(), signQueueModel.getSenderHelper(), signQueueModel.getReceiver(), signQueueModel.getReceiverHelper()), "duel-arenas-full");
                return;
            }
        } else {
            arenaModel = arenaAPI.getFreeArenaFFA();
        }
        duelRequest.setArena(arenaModel);
        this.signAPI.setSignActive(sign, signQueueModel.getSender(), signQueueModel.getReceiver(), signModel.getDuelKit());
        this.signAPI.addSignFight(signModel);
        duelAPI.startFightFour(signQueueModel.getSender(), signQueueModel.getSenderHelper(), signQueueModel.getReceiver(), signQueueModel.getReceiverHelper(), duelRequest);
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

    public void setPlayerQueue(SignQueueModel signQueueModel, DuelPlayer player, DuelPlayer set) {
        if (signQueueModel.getSender() != null && signQueueModel.getSender().getUUID().equals(player.getUUID())) {
            signQueueModel.setSender(set);
            if (signQueueModel.getSignModel().getDuelKit() == DuelKitType.CUSTOM) {
                signQueueModel.getSignModel().setKitModel(null);
            }
        } else if (signQueueModel.getReceiver() != null && signQueueModel.getReceiver().getUUID().equals(player.getUUID())) {
            signQueueModel.setReceiver(set);
        } else if (signQueueModel.getReceiverHelper() != null && signQueueModel.getReceiverHelper().getUUID().equals(player.getUUID())) {
            signQueueModel.setReceiverHelper(set);
        } else {
            signQueueModel.setSenderHelper(set);
        }
    }

    public void startFight(SignModel signModel, Sign sign, DuelPlayer sender, DuelPlayer receiver, boolean ffa) {
        DuelRequest duelRequest = DuelRequest.create(sender, receiver);
        duelRequest.setDuelKit(signModel.getDuelKit());
        duelRequest.setNumGames(1);
        duelRequest.setKitModel(signModel.getKitModel());
        duelRequest.setKitName(signModel.getKitModel().getDisplayName());
        duelRequest.setSignModel(signModel);
        ArenaModel arenaModel = null;
        if (signModel.getKitModel().isBindingArena()) {
            if (arenaAPI.getArenaFromKit(signModel.getKitModel()).isEmpty()) {
                SignUtil.clearSignsLines(sign);
                this.signAPI.setSignWait(sign, 0, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
                this.signAPI.removePlayerQueueSign(sender, receiver);
                messageConfig.sendTo(Arrays.asList(sender, receiver), "duel-arenas-full");
                return;
            }
        } else {
            arenaModel = arenaAPI.getFreeArena();
        }
        duelRequest.setArena(arenaModel);
        this.signAPI.setSignActive(sign, sender, receiver, signModel.getDuelKit());
        this.signAPI.addSignFight(signModel);
        duelAPI.startFight(duelRequest);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.onLeave(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        this.onLeave(event.getPlayer());
    }

    private void onLeave(Player bukkitPlayer) {
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        if (!this.signAPI.isQueuePlayer(player)) {
            return;
        }
        this.signAPI.getQueueFromPlayer(player).ifPresent(signQueueModel -> {
            DuelPlayer player2;
            PartyModel partyModel;
            this.setPlayerQueue(signQueueModel, player, null);
            if (this.partyAPI.isPartyPlayer(player) && (partyModel = this.partyAPI.getPartyModelFromPlayer(player)) != null && partyModel.getOwner().equals(player.getUUID()) && !partyModel.getPlayers().isEmpty()) {
                player2 = BukkitAdapter.getPlayer(partyModel.getPlayers().getFirst());
                this.setPlayerQueue(signQueueModel, player2, null);
            }
            Sign sign = (Sign) bukkitPlayer.getWorld().getBlockAt(signQueueModel.getSignModel().getBlockPosition().toLocation()).getState();
            KitModel kitModel = signQueueModel.getSignModel().getKitModel();
            String kitParam = kitModel != null
                    ? messageConfig.plainMessage("sign-server-replacer").replace("(kit)", kitModel.getDisplayName())
                    : messageConfig.plainMessage("sign-custom-replacer");
            this.signAPI.setSignWait(sign, this.getSizeQueue(signQueueModel), signQueueModel.getSignModel().getDuelType().getSize(), signQueueModel.getSignModel().getDuelKit(), kitParam);
        });
    }

}
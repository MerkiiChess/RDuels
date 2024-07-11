package ru.merkii.rduels.core.party.api.provider;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.config.settings.Settings;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.bucket.PartyBucket;
import ru.merkii.rduels.core.party.bucket.PartyFightBucket;
import ru.merkii.rduels.core.party.bucket.PartyRequestBucket;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.party.model.PartyRequestModel;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.*;
import java.util.stream.Collectors;

public class PartyAPIProvider implements PartyAPI {

    private final PartyBucket partyBucket = new PartyBucket();
    private final Settings settings = RDuels.getInstance().getSettings();
    private final PartyFightBucket partyFightBucket = new PartyFightBucket();
    private final PartyRequestBucket partyRequestBucket = new PartyRequestBucket();
    private final MessageConfiguration messageConfiguration = RDuels.getInstance().getPluginMessage();

    @Override
    public void createParty(Player player) {
        this.partyBucket.add(PartyModel.create(player, new ArrayList<>()));
        this.giveStartItems(player);
    }

    @Override
    public void leaveParty(Player player) {
        this.leaveParty(player, true);
    }

    @Override
    public void leaveParty(Player player, boolean text) {
        PartyModel partyModel = this.getPartyModelFromPlayer(player);
        if (partyModel == null) {
            return;
        }
        PartyModel fightParty = partyModel.clone();
        if (partyModel.getOwner().equals(player.getUniqueId())) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            if (this.settings.isItemOpenCustomKit()) {
                player.getInventory().setItem(this.settings.getCreateCustomKit().getSlot(), this.settings.getCreateCustomKit().build());
            }
            player.updateInventory();
            if (partyModel.getPlayers().isEmpty()) {
                this.partyBucket.remove(partyModel);
                DuelCore.INSTANCE.getDuelAPI().giveStartItems(player);
                return;
            }
            Player newOwner = Bukkit.getPlayer(partyModel.getPlayers().get(0));
            assert (newOwner != null);
            partyModel.setOwner(newOwner.getUniqueId());
            partyModel.getPlayers().remove(0);
            newOwner.sendMessage(this.messageConfiguration.getMessage("partyNewOwner"));
        } else {
            partyModel.getPlayers().remove(player.getUniqueId());
        }
        if (this.isFightParty(fightParty)) {
            this.partyFightBucket.remove(fightParty);
            this.partyFightBucket.add(partyModel);
        }
        player.sendMessage(this.messageConfiguration.getMessage("partyYouLeave"));
        if (DuelCore.INSTANCE.getDuelAPI().isFightPlayer(player)) {
            player.teleport(DuelCore.INSTANCE.getDuelAPI().getRandomSpawn());
            if (player.getGameMode() != GameMode.SURVIVAL) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
        DuelCore.INSTANCE.getDuelAPI().giveStartItems(player);
        if (text) {
            String message = this.messageConfiguration.getMessage("partyLeave").replace("(player)", player.getName());
            PlayerUtil.convertListUUID(partyModel.getPlayers()).forEach(player1 -> player1.sendMessage(message));
            Objects.requireNonNull(Bukkit.getPlayer(partyModel.getOwner())).sendMessage(message);
        }
    }

    @Override
    public void inviteParty(PartyModel partyModel, Player player) {
        this.addRequest(PartyRequestModel.create(partyModel, player.getUniqueId()));
        Player owner = Bukkit.getPlayer(partyModel.getOwner());
        this.messageConfiguration.getMessages("partyInvited").stream().map(str -> str.replace("(player)", owner.getName())).forEach(arg_0 -> player.sendMessage(arg_0));
        Component accept = Component.text(this.messageConfiguration.getMessage("acceptButton")).clickEvent(ClickEvent.runCommand("/party yes " + owner.getName()));
        Component decline = Component.text(this.messageConfiguration.getMessage("declineButton")).clickEvent(ClickEvent.runCommand("/party no " + owner.getName()));
        player.sendMessage(((TextComponent.Builder)((Object)((TextComponent.Builder)((Object)Component.text().append(accept))).append(Component.text(" ")))).append(decline));
    }

    @Override
    public void joinParty(PartyModel partyModel, Player player) {
        partyModel.getPlayers().add(player.getUniqueId());
        this.giveStartItems(player);
        player.sendMessage(this.messageConfiguration.getMessage("partyJoin"));
        Bukkit.getPlayer(partyModel.getOwner()).sendMessage(this.messageConfiguration.getMessage("partyJoin"));
        PlayerUtil.convertListUUID(partyModel.getPlayers()).forEach(players -> players.sendMessage(this.messageConfiguration.getMessage("partyJoinAll").replace("(player)", player.getName())));
        if (this.isFightParty(partyModel)) {
            DuelCore.INSTANCE.getDuelAPI().addSpectate(player, DuelCore.INSTANCE.getDuelAPI().getFightModelFromPlayer(Bukkit.getPlayer(partyModel.getOwner())));
        }
    }

    @Override
    public void addRequest(PartyRequestModel partyRequestModel) {
        this.partyRequestBucket.addRequest(partyRequestModel);
    }

    @Override
    public void removeRequest(PartyRequestModel partyRequestModel) {
        this.partyRequestBucket.removeRequest(partyRequestModel);
    }

    @Override
    @Nullable
    public PartyRequestModel getPartyRequestModel(Player sender, Player receiver) {
        for (PartyRequestModel requestModel : this.partyRequestBucket.getPartyRequestModels()) {
            if (!requestModel.getInvitedParty().getOwner().equals(sender.getUniqueId()) || !requestModel.getInvitedPlayer().equals(receiver.getUniqueId())) continue;
            if (requestModel.getEndDurationRequest() < System.currentTimeMillis()) {
                this.partyRequestBucket.removeRequest(requestModel);
                return null;
            }
            return requestModel;
        }
        return null;
    }

    @Override
    @Nullable
    public PartyModel getPartyModelFromPlayer(Player player) {
        for (PartyModel partyModel : this.partyBucket.getPartyModels()) {
            if (partyModel.getOwner().equals(player.getUniqueId())) {
                return partyModel;
            }
            for (Player player1 : partyModel.getPlayers().stream().map(Bukkit::getPlayer).collect(Collectors.toList())) {
                if (!player1.getUniqueId().equals(player.getUniqueId())) continue;
                return partyModel;
            }
        }
        return null;
    }

    @Override
    public boolean isPartyPlayer(Player player) {
        return this.getPartyModelFromPlayer(player) != null;
    }

    @Override
    public void addFightParty(PartyModel ... partyModels) {
        Arrays.asList(partyModels).forEach(this.partyFightBucket::add);
    }

    @Override
    public void removeFightParty(PartyModel ... partyModels) {
        Arrays.asList(partyModels).forEach(this.partyFightBucket::remove);
    }

    @Override
    public boolean isFightParty(PartyModel partyModel) {
        return this.partyFightBucket.getFightParty().stream().anyMatch(fightParty -> fightParty.getOwner().equals(partyModel.getOwner()));
    }

    @Override
    public List<PartyModel> getAllParty() {
        return this.partyBucket.getPartyModels();
    }

    @Override
    public void teleportToArena(DuelRequest duelRequest) {
        PartyModel receiverParty = duelRequest.getReceiverParty();
        PartyModel senderParty = duelRequest.getSenderParty();
        this.teleportToArena(senderParty, receiverParty, duelRequest.getArena());
    }

    @Override
    public void teleportToArena(DuelFightModel duelFightModel) {
        assert (duelFightModel.getSenderParty() != null);
        assert (duelFightModel.getReceiverParty() != null);
        this.teleportToArena(duelFightModel.getSenderParty(), duelFightModel.getReceiverParty(), duelFightModel.getArenaModel());
    }

    @Override
    public void teleportToArena(PartyModel senderParty, PartyModel receiverParty, ArenaModel arenaModel) {
        Player sender = Bukkit.getPlayer(senderParty.getOwner());
        Player receiver = Bukkit.getPlayer(receiverParty.getOwner());
        assert (sender != null);
        sender.teleport(arenaModel.getFfaPositions().get(1).toLocation());
        if (!senderParty.getPlayers().isEmpty()) {
            for (int i = 0; i <= senderParty.getPlayers().size(); ++i) {
                Player player = Bukkit.getPlayer(senderParty.getPlayers().get(i));
                assert (player != null);
                player.teleport(arenaModel.getFfaPositions().get(i + 2).toLocation());
            }
        }
        assert (receiver != null);
        int add = PartyCore.INSTANCE.getPartyConfig().getMaxPartySize() + 1;
        receiver.teleport(arenaModel.getFfaPositions().get(add).toLocation());
        if (!receiverParty.getPlayers().isEmpty()) {
            for (int i = 0; i <= receiverParty.getPlayers().size(); ++i) {
                Player player = Bukkit.getPlayer(senderParty.getPlayers().get(i));
                assert (player != null);
                player.teleport(arenaModel.getFfaPositions().get(i + add + 1).toLocation());
            }
        }
    }

    @Override
    public void giveStartItems(Player ... players) {
        Arrays.asList(players).forEach(player -> {
            PlayerInventory inventory = player.getInventory();
            inventory.setArmorContents(null);
            inventory.clear();
            inventory.setItem(this.settings.getFightParty().getSlot(), this.settings.getFightParty().build());
            inventory.setItem(this.settings.getLeaveParty().getSlot(), this.settings.getLeaveParty().build());
            player.updateInventory();
        });
    }


}

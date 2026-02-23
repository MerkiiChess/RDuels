package ru.merkii.rduels.core.party.api.provider;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.bucket.PartyBucket;
import ru.merkii.rduels.core.party.bucket.PartyFightBucket;
import ru.merkii.rduels.core.party.bucket.PartyRequestBucket;
import ru.merkii.rduels.core.party.config.PartyConfiguration;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.party.model.PartyRequestModel;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.util.PlayerUtil;
import java.util.*;

@Singleton
public class PartyAPIProvider implements PartyAPI {

    private final PartyConfiguration partyConfiguration;
    private final PartyBucket partyBucket;
    private final SettingsConfiguration settings ;
    private final PartyFightBucket partyFightBucket;
    private final PartyRequestBucket partyRequestBucket;
    private final MessageConfig messageConfig;
    @Inject
    public DuelAPI duelAPI;

    @Inject
    public PartyAPIProvider(PartyConfiguration partyConfiguration, PartyBucket partyBucket, SettingsConfiguration settings, PartyFightBucket partyFightBucket, PartyRequestBucket partyRequestBucket, MessageConfig messageConfig) {
        this.partyConfiguration = partyConfiguration;
        this.partyBucket = partyBucket;
        this.settings = settings;
        this.partyFightBucket = partyFightBucket;
        this.partyRequestBucket = partyRequestBucket;
        this.messageConfig = messageConfig;
    }

    @Override
    public void createParty(DuelPlayer player) {
        this.partyBucket.add(PartyModel.create(player, new ArrayList<>()));
        this.giveStartItems(player);
    }

    @Override
    public void leaveParty(DuelPlayer player) {
        this.leaveParty(player, true);
    }

    @Override
    public void leaveParty(DuelPlayer player, boolean sendMessages) {
        PartyModel partyModel = this.getPartyModelFromPlayer(player);
        if (partyModel == null) {
            return;
        }

        boolean isOwner = partyModel.getOwner().equals(player.getUUID());
        boolean wasFighting = this.isFightParty(partyModel);

        if (isOwner) {
            handleOwnerLeave(partyModel, player);
        } else {
            partyModel.getPlayers().remove(player.getUUID());
        }

        if (wasFighting) {
            this.partyFightBucket.remove(partyModel);
        }

        resetPlayerInventory(player);
        if (duelAPI.isFightPlayer(player)) {
            player.teleport(duelAPI.getRandomSpawn());
            player.setGameMode(GameMode.SURVIVAL);
        }
        duelAPI.giveStartItems(player);

        messageConfig.sendTo(player, "party-you-leave");

        if (sendMessages) {
            Component message = messageConfig.message(Placeholder.wrapped("(player)", player.getName()), "party-leave");
            getAllPlayersInParty(partyModel).forEach(member -> member.sendMessage(message));
        }
    }

    private void handleOwnerLeave(PartyModel partyModel, DuelPlayer player) {
        resetPlayerInventory(player);

        if (partyModel.getPlayers().isEmpty()) {
            this.partyBucket.remove(partyModel);
            return;
        }

        Player newOwner = Bukkit.getPlayer(partyModel.getPlayers().get(0));
        if (newOwner == null) {
            disbandParty(partyModel);
            return;
        }

        partyModel.setOwner(newOwner.getUniqueId());
        partyModel.getPlayers().remove(0);
        messageConfig.sendTo(newOwner, "party-new-owner");
    }

    private void disbandParty(PartyModel partyModel) {
        getAllPlayersInParty(partyModel).forEach(this::resetPlayerInventory);
        this.partyBucket.remove(partyModel);
    }

    private void resetPlayerInventory(DuelPlayer player) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        bukkitPlayer.getInventory().clear();
        bukkitPlayer.getInventory().setArmorContents(null);
        if (this.settings.itemOpenCustomKit()) {
            bukkitPlayer.getInventory().setItem(this.settings.createCustomKit().slot(), this.settings.createCustomKit().build());
        }
        bukkitPlayer.updateInventory();
    }

    @Override
    public void inviteParty(PartyModel partyModel, DuelPlayer player) {
        this.addRequest(PartyRequestModel.create(partyModel, player.getUUID()));
        DuelPlayer owner = BukkitAdapter.getPlayer(partyModel.getOwner());

        messageConfig.sendTo(player, Placeholder.wrapped("(player)", owner.getName()), "party-invited");

        Component accept = messageConfig.message("accept-button")
                .clickEvent(ClickEvent.runCommand("/party yes " + owner.getName()));
        Component decline = messageConfig.message("decline-button")
                .clickEvent(ClickEvent.runCommand("/party no " + owner.getName()));
        player.sendMessage(accept.append(Component.space()).append(decline));
    }

    @Override
    public void joinParty(PartyModel partyModel, DuelPlayer player) {
        partyModel.getPlayers().add(player.getUUID());
        this.giveStartItems(player);

        Component joinMessage = messageConfig.message("party-join");
        player.sendMessage(joinMessage);

        DuelPlayer owner = BukkitAdapter.getPlayer(partyModel.getOwner());
        owner.sendMessage(joinMessage);
        Component message = messageConfig.message(Placeholder.wrapped("(player)", player.getName()), "party-join-all");
        PlayerUtil.convertListUUID(partyModel.getPlayers()).forEach(member -> member.sendMessage(message));

        if (this.isFightParty(partyModel)) {
            DuelFightModel fightModel = duelAPI.getFightModelFromPlayer(owner);
            if (fightModel != null) {
                duelAPI.addSpectate(player, fightModel);
            }
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
    public PartyRequestModel getPartyRequestModel(DuelPlayer sender, DuelPlayer receiver) {
        UUID senderUUID = sender.getUUID();
        UUID receiverUUID = receiver.getUUID();
        Iterator<PartyRequestModel> iterator = this.partyRequestBucket.getPartyRequestModels().iterator();
        while (iterator.hasNext()) {
            PartyRequestModel requestModel = iterator.next();
            if (requestModel.getInvitedParty().getOwner().equals(senderUUID) && requestModel.getInvitedPlayer().equals(receiverUUID)) {
                if (requestModel.getEndDurationRequest() < System.currentTimeMillis()) {
                    iterator.remove();
                    continue;
                }
                return requestModel;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public PartyModel getPartyModelFromPlayer(DuelPlayer player) {
        UUID playerUUID = player.getUUID();
        for (PartyModel partyModel : this.partyBucket.getPartyModels()) {
            if (partyModel.getOwner().equals(playerUUID) || partyModel.getPlayers().contains(playerUUID)) {
                return partyModel;
            }
        }
        return null;
    }

    @Override
    public boolean isPartyPlayer(DuelPlayer player) {
        return this.getPartyModelFromPlayer(player) != null;
    }

    @Override
    public void addFightParty(PartyModel... partyModels) {
        Arrays.asList(partyModels).forEach(this.partyFightBucket::add);
    }

    @Override
    public void removeFightParty(PartyModel... partyModels) {
        Arrays.asList(partyModels).forEach(this.partyFightBucket::remove);
    }

    @Override
    public boolean isFightParty(PartyModel partyModel) {
        return this.partyFightBucket.getFightParty().stream()
                .anyMatch(fightParty -> fightParty.getOwner().equals(partyModel.getOwner()));
    }

    @Override
    public List<PartyModel> getAllParty() {
        return this.partyBucket.getPartyModels();
    }

    @Override
    public void teleportToArena(DuelRequest duelRequest) {
        if (duelRequest.getSenderParty() == null || duelRequest.getReceiverParty() == null) {
            return;
        }
        this.teleportToArena(duelRequest.getSenderParty(), duelRequest.getReceiverParty(), duelRequest.getArena());
    }

    @Override
    public void teleportToArena(DuelFightModel duelFightModel) {
        if (duelFightModel.getSenderParty() == null || duelFightModel.getReceiverParty() == null) {
            return;
        }
        this.teleportToArena(duelFightModel.getSenderParty(), duelFightModel.getReceiverParty(), duelFightModel.getArenaModel());
    }

    @Override
    public void teleportToArena(PartyModel senderParty, PartyModel receiverParty, ArenaModel arenaModel) {
        Map<Integer, EntityPosition> positions = arenaModel.getFfaPositions();

        DuelPlayer senderOwner = BukkitAdapter.getPlayer(senderParty.getOwner());
        senderOwner.teleport(positions.get(1));

        List<UUID> senderMembers = senderParty.getPlayers();
        for (int i = 0; i < senderMembers.size(); i++) {
            DuelPlayer member = BukkitAdapter.getPlayer(senderMembers.get(i));
            member.teleport(positions.get(i + 2));
        }

        int offset = partyConfiguration.maxPartySize() + 1;
        DuelPlayer receiverOwner = BukkitAdapter.getPlayer(receiverParty.getOwner());
        receiverOwner.teleport(positions.get(offset));

        List<UUID> receiverMembers = receiverParty.getPlayers();
        for (int i = 0; i < receiverMembers.size(); i++) {
            DuelPlayer member = BukkitAdapter.getPlayer(receiverMembers.get(i));
            member.teleport(positions.get(offset + i + 1));
        }
    }

    @Override
    public void giveStartItems(DuelPlayer... players) {
        Arrays.asList(players).forEach(player -> {
            Player bukkitPlayer = BukkitAdapter.adapt(player);
            PlayerInventory inventory = bukkitPlayer.getInventory();
            inventory.setArmorContents(null);
            inventory.clear();
            inventory.setItem(this.settings.fightParty().slot(), this.settings.fightParty().build());
            inventory.setItem(this.settings.leaveParty().slot(), this.settings.leaveParty().build());
            bukkitPlayer.updateInventory();
        });
    }

    private List<DuelPlayer> getAllPlayersInParty(PartyModel partyModel) {
        List<DuelPlayer> players = new ArrayList<>(PlayerUtil.duelPlayersConvertListUUID(partyModel.getPlayers()));
        DuelPlayer owner = BukkitAdapter.getPlayer(partyModel.getOwner());
        players.add(owner);
        return players;
    }
}
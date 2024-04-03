package ru.merkii.rduels.core.party.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.party.model.PartyRequestModel;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CommandAlias("party")
public class PartyCommand extends BaseCommand {

    private final PartyCore partyCore = PartyCore.INSTANCE;
    private final PartyAPI partyAPI = partyCore.getPartyAPI();
    private final MessageConfiguration messageConfiguration = RDuels.getInstance().getPluginMessage();

    @Default
    @Description("Помощь по командам")
    public void onParty(Player player) {
        this.messageConfiguration.getMessages("partyHelp").forEach(player::sendMessage);
    }

    @Subcommand("list")
    @Description("Вывести список всех игроков")
    public void partyList(Player player) {
        if (!this.partyAPI.isPartyPlayer(player)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNo"));
            return;
        }
        PartyModel partyModel = this.partyAPI.getPartyModelFromPlayer(player);
        if (partyModel == null) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNo"));
            return;
        }
        StringBuilder players = new StringBuilder();
        partyModel.getPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).map(Player::getName).forEach(name -> players.append(name).append(", "));
        this.messageConfiguration.getMessages("partyPlayers")
                .stream()
                .map(str -> str.replace("(owner)", Bukkit.getPlayer(partyModel.getOwner()).getName()))
                .map(str -> str.replace("(players)", players.toString()))
                .forEach(player::sendMessage);
    }

    @Subcommand("disband")
    @Description("Распустить пати")
    public void disband(Player player) {
        if (!this.partyAPI.isPartyPlayer(player)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNo"));
            return;
        }
        PartyModel partyModel = this.partyAPI.getPartyModelFromPlayer(player);
        if (partyModel == null) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNo"));
            return;
        }
        if (!partyModel.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNoOwner"));
            return;
        }
        PlayerUtil.convertListUUID(partyModel.getPlayers()).forEach(partyPlayer -> this.partyAPI.leaveParty(partyPlayer, false));
        this.partyAPI.leaveParty(player);
        player.sendMessage(this.messageConfiguration.getMessage("partyDisband"));
    }

    @Subcommand("create")
    @Description("Создать пати")
    public void create(Player player) {
        if (this.partyAPI.isPartyPlayer(player)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyAlready"));
            return;
        }
        this.partyAPI.createParty(player);
    }

    @Subcommand("leave")
    @Description("Выйти с пати")
    public void onLeaveParty(Player player) {
        if (!this.partyAPI.isPartyPlayer(player)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNo"));
            return;
        }
        this.partyAPI.leaveParty(player);
    }

    @Subcommand("kick")
    @CommandCompletion("@allplayers")
    @Description("кикнуть игрока с пати")
    public void onPartyKick(Player player, @Name("ник") String receiverName) {
        if (!this.partyAPI.isPartyPlayer(player)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNo"));
            return;
        }
        PartyModel partyModel = this.partyAPI.getPartyModelFromPlayer(player);
        if (partyModel == null) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNo"));
            return;
        }
        if (!partyModel.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNoOwner"));
            return;
        }

        Player receiver = Bukkit.getPlayerExact(receiverName);

        if (receiver == null) {
            player.sendMessage(this.messageConfiguration.getMessage("duelOffline").replace("(player)", receiverName));
            return;
        }
        if (!this.partyAPI.isPartyPlayer(receiver)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNoPlayer").replace("(player)", receiver.getName()));
            return;
        }
        PartyModel partyModelReceiver = this.partyAPI.getPartyModelFromPlayer(receiver);
        if (partyModelReceiver == null) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNoPlayer").replace("(player)", receiver.getName()));
            return;
        }
        if (!partyModelReceiver.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNoYourPlayer").replace("(player)", receiver.getName()));
            return;
        }
        this.partyAPI.leaveParty(receiver);
    }

    @Subcommand("invite")
    @CommandCompletion("@allplayers")
    @Description("Пригласить игрока в пати")
    public void onPartyInvite(Player player, @Name("игрок") String receiverName) {
        if (!this.partyAPI.isPartyPlayer(player)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNo"));
            return;
        }
        PartyModel partyModel = this.partyAPI.getPartyModelFromPlayer(player);
        if (partyModel == null) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNo"));
            return;
        }
        if (!partyModel.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNoOwner"));
            return;
        }

        Player receiver = Bukkit.getPlayerExact(receiverName);

        if (receiver == null) {
            player.sendMessage(this.messageConfiguration.getMessage("duelOffline").replace("(player)", receiverName));
            return;
        }
        if (this.partyAPI.isPartyPlayer(receiver)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyAlreadyPlayer").replace("(player)", receiver.getName()));
            return;
        }
        if (partyModel.getPlayers().size() + 1 == this.partyCore.getPartyConfig().getMaxPartySize()) {
            player.sendMessage(this.messageConfiguration.getMessage("partyFullSender"));
            return;
        }
        if (this.partyAPI.getPartyRequestModel(player, receiver) != null) {
            player.sendMessage(this.messageConfiguration.getMessage("partyAlreadyInvite").replace("(player)", receiver.getName()));
            return;
        }
        player.sendMessage(this.messageConfiguration.getMessage("partyInvite").replace("(player)", player.getName()));
        this.partyAPI.inviteParty(partyModel, receiver);
    }

    @Subcommand("yes")
    @CommandCompletion("@allplayers")
    @Description("Принять приглашение на пати")
    public void onPartyYes(Player player, @Name("ник") String receiverName) {
        if (this.partyAPI.isPartyPlayer(player)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyAlready"));
            return;
        }

        Player receiver = Bukkit.getPlayerExact(receiverName);

        if (receiver == null) {
            player.sendMessage(this.messageConfiguration.getMessage("duelOffline").replace("(player)", receiverName));
            return;
        }
        if (!this.partyAPI.isPartyPlayer(receiver)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNoPlayer").replace("(player)", player.getName()));
            return;
        }
        PartyRequestModel requestModel = this.partyAPI.getPartyRequestModel(receiver, player);
        if (requestModel == null) {
            player.sendMessage(this.messageConfiguration.getMessage("partyEndDuration"));
            return;
        }
        this.partyAPI.removeRequest(requestModel);
        if (requestModel.getInvitedParty().getPlayers().size() == this.partyCore.getPartyConfig().getMaxPartySize()) {
            player.sendMessage(this.messageConfiguration.getMessage("partyFull").replace("(player)", player.getName()));
            return;
        }
        this.partyAPI.joinParty(requestModel.getInvitedParty(), player);
    }

    @Subcommand("no")
    @CommandCompletion("@allplayers")
    @Description("Отклонить приглашение на пати")
    public void onPartyNo(Player player, @Name("ник") String receiverName) {
        if (this.partyAPI.isPartyPlayer(player)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyAlready"));
            return;
        }

        Player receiver = Bukkit.getPlayerExact(receiverName);

        if (receiver == null) {
            player.sendMessage(this.messageConfiguration.getMessage("duelOffline").replace("(player)", receiverName));
            return;
        }
        if (!this.partyAPI.isPartyPlayer(receiver)) {
            player.sendMessage(this.messageConfiguration.getMessage("partyNoPlayer").replace("(player)", player.getName()));
            return;
        }
        PartyRequestModel requestModel = this.partyAPI.getPartyRequestModel(receiver, player);
        if (requestModel == null) {
            player.sendMessage(this.messageConfiguration.getMessage("partyEndDuration"));
            return;
        }
        this.partyAPI.removeRequest(requestModel);
        player.sendMessage(this.messageConfiguration.getMessage("partyDecline"));
        receiver.sendMessage(this.messageConfiguration.getMessage("partyDeclineSender").replace("(player)", player.getName()));
    }

}

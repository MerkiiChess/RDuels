package ru.merkii.rduels.core.party.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.config.PartyConfiguration;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.party.model.PartyRequestModel;
import ru.merkii.rduels.lamp.suggestion.AllPlayers;

@Command({"party", "p"})
@Singleton
public class PartyCommand {

    private final PartyAPI partyAPI;
    private final PartyConfiguration partyConfiguration;
    private final MessageConfig messageConfig;

    @Inject
    public PartyCommand(PartyAPI partyAPI, PartyConfiguration partyConfiguration, MessageConfig messageConfig) {
        this.partyAPI = partyAPI;
        this.partyConfiguration = partyConfiguration;
        this.messageConfig = messageConfig;
    }

    @Command("create")
    @Description("Создать пати")
    public void create(BukkitCommandActor actor) {
        Player bukkitPlayer = actor.asPlayer();
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        if (partyAPI.isPartyPlayer(player)) {
            messageConfig.sendTo(player, "party-already");
            return;
        }
        partyAPI.createParty(player);
        messageConfig.sendTo(player, "party-created");
    }

    @Command("invite")
    @Description("Пригласить в пати")
    public void invite(BukkitCommandActor actor, @SuggestWith(AllPlayers.class) Player targetBukkit) {
        Player bukkitPlayer = actor.asPlayer();
        if (targetBukkit == null) {
            // TODO Сообщение если еблана нет в сети
            return;
        }
        DuelPlayer target = BukkitAdapter.adapt(targetBukkit);
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        PartyModel partyModel = partyAPI.getPartyModelFromPlayer(player);
        if (partyModel == null) {
            messageConfig.sendTo(player, "party-no");
            return;
        }
        if (!partyModel.getOwner().equals(player.getUUID())) {
            messageConfig.sendTo(player, "party-no-owner");
            return;
        }
        if (partyModel.getPlayers().size() >= partyConfiguration.maxPartySize() - 1) {
            messageConfig.sendTo(player, "party-full-sender");
            return;
        }
        if (partyAPI.isPartyPlayer(target)) {
            messageConfig.sendTo(player, Placeholder.wrapped("(player)", target.getName()), "party-already-player");
            return;
        }
        if (partyAPI.getPartyRequestModel(player, target) != null) {
            messageConfig.sendTo(player, Placeholder.wrapped("(player)", target.getName()), "party-already-invite");
            return;
        }
        partyAPI.inviteParty(partyModel, target);
        messageConfig.sendTo(player, Placeholder.wrapped("(player)", target.getName()), "party-invite");
    }

    @Command("yes")
    @Description("Принять приглашение в пати")
    public void yes(BukkitCommandActor actor, @SuggestWith(AllPlayers.class) Player bukkitSender) {
        Player bukkitPlayer = actor.asPlayer();
        DuelPlayer sender = BukkitAdapter.adapt(bukkitSender);
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        PartyRequestModel request = partyAPI.getPartyRequestModel(sender, player);
        if (request == null) {
            messageConfig.sendTo(player, "party-end-duration");
            return;
        }
        PartyModel partyModel = request.getInvitedParty();
        if (partyModel.getPlayers().size() >= partyConfiguration.maxPartySize() - 1) {
            messageConfig.sendTo(player, Placeholder.wrapped("(player)", sender.getName()), "party-full");
            partyAPI.removeRequest(request);
            return;
        }
        partyAPI.removeRequest(request);
        partyAPI.joinParty(partyModel, player);
    }

    @Command("no")
    @Description("Отклонить приглашение в пати")
    public void no(BukkitCommandActor actor, @SuggestWith(AllPlayers.class) Player bukkitSender) {
        Player bukkitPlayer = actor.asPlayer();
        DuelPlayer sender = BukkitAdapter.adapt(bukkitSender);
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        PartyRequestModel request = partyAPI.getPartyRequestModel(sender, player);
        if (request == null) {
            messageConfig.sendTo(player, "party-end-duration");messageConfig.sendTo(player, "party-end-duration");
            return;
        }
        partyAPI.removeRequest(request);
        messageConfig.sendTo(player, "party-decline");
        messageConfig.sendTo(sender, Placeholder.wrapped("(player)", player.getName()), "party-decline-sender");
    }

    @Command("leave")
    @Description("Выйти из пати")
    public void leave(BukkitCommandActor actor) {
        Player bukkitPlayer = actor.asPlayer();
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        if (!partyAPI.isPartyPlayer(player)) {
            messageConfig.sendTo(player, "party-no");
            return;
        }
        partyAPI.leaveParty(player);
    }

    @Command("disband")
    @Description("Распустить пати")
    public void disband(BukkitCommandActor actor) {
        Player bukkitPlayer = actor.asPlayer();
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        PartyModel partyModel = partyAPI.getPartyModelFromPlayer(player);
        if (partyModel == null) {
            messageConfig.sendTo(player, "party-no");
            return;
        }
        if (!partyModel.getOwner().equals(player.getUUID())) {
            messageConfig.sendTo(player, "party-no-owner");
            return;
        }
        partyAPI.leaveParty(player, false);
        messageConfig.sendTo(player, "party-disband");
    }

    @Command("kick")
    @Description("Кикнуть игрока из пати")
    public void kick(BukkitCommandActor actor, @SuggestWith(AllPlayers.class) Player bukkitTarget) {
        Player bukkitPlayer = actor.asPlayer();
        DuelPlayer target = BukkitAdapter.adapt(bukkitTarget);
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        PartyModel partyModel = partyAPI.getPartyModelFromPlayer(player);
        if (partyModel == null) {
            messageConfig.sendTo(player, "party-no");
            return;
        }
        if (!partyModel.getOwner().equals(player.getUUID())) {
            messageConfig.sendTo(player, "party-no-owner");
            return;
        }
        if (!partyModel.getPlayers().contains(target.getUUID())) {
            messageConfig.sendTo(player, Placeholder.wrapped("(player)", target.getName()), "party-no-your-player");
            return;
        }
        partyAPI.leaveParty(target);
        messageConfig.sendTo(player, Placeholder.wrapped("(player)", target.getName()), "party-kick");
    }
    @Command("help")
    @Description("Помощь по командам пати")
    public void help(BukkitCommandActor actor) {
        messageConfig.sendTo(actor.requirePlayer(), "party-help");
    }
}
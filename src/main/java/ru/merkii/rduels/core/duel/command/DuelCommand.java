package ru.merkii.rduels.core.duel.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.menu.DuelChoiceKitMenu;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.lamp.suggestion.AllPlayers;
import ru.merkii.rduels.util.TimeUtil;

import java.util.List;

@Command("duel")
@Singleton
public class DuelCommand {

    private final MessageConfig config;
    private final DuelAPI duelAPI;

    @Inject
    public DuelCommand(DuelAPI duelAPI, MessageConfig config) {
        this.config = config;
        this.duelAPI = duelAPI;
    }

    @Command("duel")
    @Description("Вызвать игрока на дуэль.")
    public void onDuel(BukkitCommandActor actor, @SuggestWith(AllPlayers.class) DuelPlayer receiver) {
        Player senderPlayerBukkit = actor.asPlayer();
        DuelPlayer senderPlayer = BukkitAdapter.adapt(senderPlayerBukkit);

        if (senderPlayer.getUUID().equals(receiver.getUUID())) {
            config.sendTo(senderPlayer, "duel-you");
            return;
        }

        String receiverName = receiver.getName();

        if (duelAPI.isFightPlayer(receiver)) {
            config.sendTo(senderPlayer, Placeholder.wrapped("(player)", receiverName), "duel-already-fight");
            return;
        }

        DuelRequest duelRequest = duelAPI.getRequestFromSender(senderPlayer, receiver);
        if (duelRequest != null && duelRequest.getTime() > System.currentTimeMillis()) {
            config.sendTo(
                    senderPlayer,
                    Placeholder.wrapped("(time)", TimeUtil.getTimeInMaxUnit(duelRequest.getTime() - System.currentTimeMillis())),
                    "duel-already-request"
            );
            duelAPI.removeRequest(duelRequest);
        }

        new DuelChoiceKitMenu().open(BukkitAdapter.adapt(senderPlayer), DuelRequest.create(senderPlayer, receiver), false);
    }

    @Command("duel yes")
    @Description("Принять вызов на дуэль.")
    public void onYes(BukkitCommandActor actor, @SuggestWith(AllPlayers.class) DuelPlayer sender) {
        Player receiverBukkit = actor.asPlayer();
        DuelPlayer receiver = BukkitAdapter.adapt(receiverBukkit);

        List<DuelRequest> requests = duelAPI.getRequestsFromReceiver(receiver);
        if (requests == null || requests.isEmpty()) {
            config.sendTo(receiver, "duel-request-empty");
            return;
        }
        DuelRequest request = requests.stream().filter(duelRequest -> duelRequest.getSender().getUUID().equals(sender.getUUID())).findFirst().orElse(null);

        String senderName = sender.getName();

        if (request == null) {
            config.sendTo(receiver, Placeholder.wrapped("(player)", senderName), "duel-player-not-request");
            return;
        }

        duelAPI.removeRequest(request);

        if (duelAPI.isFightPlayer(sender)) {
            config.sendTo(sender, Placeholder.wrapped("(player)", senderName), "duel-already-fight");
            return;
        }

        if (request.getTime() < System.currentTimeMillis()) {
            config.sendTo(receiver, Placeholder.wrapped("(player)", senderName), "duel-player-not-request");
            return;
        }

        duelAPI.startFight(request);
    }

    @Command("duel no")
    @Description("Отклонить вызов на дуэль.")
    public void onNo(BukkitCommandActor actor, @SuggestWith(AllPlayers.class) DuelPlayer sender) {
        Player receiverBukkit = actor.asPlayer();
        DuelPlayer receiver = BukkitAdapter.adapt(receiverBukkit);

        List<DuelRequest> requests = duelAPI.getRequestsFromReceiver(receiver);
        if (requests == null || requests.isEmpty()) {
            config.sendTo(receiver, "duel-request-empty");
            return;
        }
        DuelRequest request = requests.stream().filter(duelRequest -> duelRequest.getSender().getUUID().equals(sender.getUUID())).findFirst().orElse(null);

        String senderName = sender.getName();

        if (request == null) {
            config.sendTo(receiver, Placeholder.wrapped("(player)", senderName), "duel-player-not-request");
            return;
        }

        duelAPI.removeRequest(request);

        if (request.getTime() < System.currentTimeMillis()) {
            config.sendTo(receiver, "duel-request-time");
            return;
        }
        config.sendTo(receiver, "duel-no");
        config.sendTo(sender, Placeholder.wrapped("(player)", receiver.getName()), "duel-no-sender");
    }

}

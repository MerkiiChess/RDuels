package ru.merkii.rduels.core.duel.request;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.bucket.DuelRequestsBucket;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.party.model.PartyModel;

import java.util.List;
import java.util.UUID;

import static ru.merkii.rduels.config.Placeholder.Placeholders;

@Singleton
public class DuelRequestServiceImpl implements DuelRequestService {

    private final DuelRequestsBucket requestsBucket;
    private final MessageConfig config;

    @Inject
    public DuelRequestServiceImpl(DuelRequestsBucket requestsBucket, MessageConfig config) {
        this.requestsBucket = requestsBucket;
        this.config = config;
    }

    @Override
    public void addRequest(DuelRequest duelRequest) {
        this.requestsBucket.addRequest(duelRequest);
        DuelPlayer sender = getOwnerOrPlayer(duelRequest.getSenderParty(), duelRequest.getSender());
        DuelPlayer receiver = getOwnerOrPlayer(duelRequest.getReceiverParty(), duelRequest.getReceiver());

        if (sender == null || receiver == null) return;

        config.sendTo(sender, Placeholder.wrapped("(player)", receiver.getName()), "request-sender");

        Placeholders placeholder = Placeholders.of(
                Placeholder.of("(player)", sender.getName()),
                Placeholder.of("(kit)",
                        duelRequest.getDuelKit() == DuelKitType.CUSTOM
                                ? config.plainMessage("custom-replacer")
                                : duelRequest.getKitModel().getDisplayName()),
                Placeholder.of("(arena)", duelRequest.getArena().getArenaName()),
                Placeholder.of("(numGames)", String.valueOf(duelRequest.getNumGames()))
        );

        config.sendTo(receiver, placeholder, "request-receiver");



        Component accept = config.message("accept-button")
                .clickEvent(ClickEvent.runCommand("/duel yes " + sender.getName()));
        Component decline = config.message("decline-button")
                .clickEvent(ClickEvent.runCommand("/duel no " + sender.getName()));
        receiver.sendMessage(accept.append(Component.space()).append(decline));
    }

    private DuelPlayer getOwnerOrPlayer(PartyModel party, DuelPlayer player) {
        return party != null ? BukkitAdapter.getPlayer(party.getOwner()) : player;
    }

    @Override
    public void removeRequest(DuelRequest duelRequest) {
        this.requestsBucket.removeRequest(duelRequest);
    }

    @Override
    public List<DuelRequest> getRequestsFromReceiver(DuelPlayer receiver) {
        UUID uuid = receiver.getUUID();
        return requestsBucket.getRequests().stream()
                .filter(r -> r.getReceiver().getUUID().equals(uuid))
                .toList();
    }

    @Override
    public DuelRequest getRequestFromSender(DuelPlayer sender, DuelPlayer receiver) {
        UUID s = sender.getUUID(), r = receiver.getUUID();
        return requestsBucket.getRequests().stream()
                .filter(req -> req.getSender().getUUID().equals(s) && req.getReceiver().getUUID().equals(r))
                .findFirst().orElse(null);
    }
}

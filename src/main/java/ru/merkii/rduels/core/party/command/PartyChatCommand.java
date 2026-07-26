package ru.merkii.rduels.core.party.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.bucket.PartyChatBucket;

/**
 * {@code /pc} toggles party-chat mode: while on, the player's normal chat is routed to
 * their party (see {@link ru.merkii.rduels.core.party.listener.PartyChatListener}).
 */
@Singleton
public class PartyChatCommand {

    private final PartyChatBucket partyChatBucket;
    private final PartyAPI partyAPI;
    private final MessageConfig messages;

    @Inject
    public PartyChatCommand(PartyChatBucket partyChatBucket, PartyAPI partyAPI, MessageConfig messages) {
        this.partyChatBucket = partyChatBucket;
        this.partyAPI = partyAPI;
        this.messages = messages;
    }

    @Command({"pc", "partychat"})
    public void onToggle(Player player) {
        if (!partyAPI.isPartyPlayer(BukkitAdapter.adapt(player))) {
            messages.sendTo(player, "party-no");
            return;
        }
        boolean enabled = partyChatBucket.toggle(player.getUniqueId());
        messages.sendTo(player, enabled ? "party-chat-on" : "party-chat-off");
    }

}

package ru.merkii.rduels.core.party.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.bucket.PartyChatBucket;
import ru.merkii.rduels.core.party.config.PartyConfiguration;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.util.PlayerUtil;

/**
 * Routes chat of players in party-chat mode to their party only, cancelling the public
 * message. Uses Paper's {@link AsyncChatEvent}.
 */
@Singleton
public class PartyChatListener implements Listener {

    private final PartyChatBucket partyChatBucket;
    private final PartyAPI partyAPI;
    private final PartyConfiguration config;

    @Inject
    public PartyChatListener(PartyChatBucket partyChatBucket, PartyAPI partyAPI, PartyConfiguration config) {
        this.partyChatBucket = partyChatBucket;
        this.partyAPI = partyAPI;
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (!partyChatBucket.isEnabled(player.getUniqueId())) {
            return;
        }
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        PartyModel party = partyAPI.getPartyModelFromPlayer(duelPlayer);
        if (party == null) {
            // Left the party while in party-chat mode — drop the flag and let it be public.
            partyChatBucket.remove(player.getUniqueId());
            return;
        }
        event.setCancelled(true);

        String plain = PlainTextComponentSerializer.plainText().serialize(event.message());
        Component formatted = MiniMessage.miniMessage().deserialize(config.chatFormat()
                .replace("(player)", player.getName())
                .replace("(message)", plain));

        members(party).forEach(member -> member.sendMessage(formatted));
    }

    private java.util.List<Player> members(PartyModel party) {
        java.util.List<Player> players = PlayerUtil.convertListUUID(party.getPlayers());
        Player owner = org.bukkit.Bukkit.getPlayer(party.getOwner());
        if (owner != null) {
            players.add(owner);
        }
        return players;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        partyChatBucket.remove(event.getPlayer().getUniqueId());
    }

}

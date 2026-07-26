package ru.merkii.rduels.core.party.scheduler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.config.PartyConfiguration;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.util.PlayerUtil;

/** Periodically reminds party members of their party. */
public class PartyBroadcastScheduler extends BukkitRunnable {

    private final PartyAPI partyAPI;
    private final PartyConfiguration config;

    private PartyBroadcastScheduler(PartyAPI partyAPI, PartyConfiguration config) {
        this.partyAPI = partyAPI;
        this.config = config;
    }

    public static PartyBroadcastScheduler start(RDuels plugin, PartyAPI partyAPI, PartyConfiguration config) {
        PartyBroadcastScheduler scheduler = new PartyBroadcastScheduler(partyAPI, config);
        long interval = Math.max(1, config.broadcastIntervalSeconds()) * 20L;
        scheduler.runTaskTimer(plugin, interval, interval);
        return scheduler;
    }

    @Override
    public void run() {
        Component message = MiniMessage.miniMessage().deserialize(config.broadcastMessage());
        for (PartyModel party : partyAPI.getAllParty()) {
            PlayerUtil.convertListUUID(party.getPlayers()).forEach(member -> member.sendMessage(message));
            Player owner = Bukkit.getPlayer(party.getOwner());
            if (owner != null) {
                owner.sendMessage(message);
            }
        }
    }
}

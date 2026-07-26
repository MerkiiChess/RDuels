package ru.merkii.rduels.core.party;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.command.PartyChatCommand;
import ru.merkii.rduels.core.party.command.PartyCommand;
import ru.merkii.rduels.core.party.config.PartyConfiguration;
import ru.merkii.rduels.core.party.scheduler.PartyBroadcastScheduler;

@Getter
@Singleton
public class PartyCore implements Core {

    private final Lamp<BukkitCommandActor> lamp;
    private final PartyConfiguration config;
    private PartyBroadcastScheduler broadcastScheduler;

    @Inject
    public PartyCore(Lamp<BukkitCommandActor> lamp, PartyConfiguration config) {
        this.lamp = lamp;
        this.config = config;
    }

    @Override
    public void enable(RDuels plugin) {
        lamp.register(RDuels.beanScope().get(PartyCommand.class));
        lamp.register(RDuels.beanScope().get(PartyChatCommand.class));
        if (config.broadcastEnabled()) {
            this.broadcastScheduler = PartyBroadcastScheduler.start(
                    plugin, RDuels.beanScope().get(PartyAPI.class), config);
        }
    }

    @Override
    public void disable(RDuels plugin) {
        if (this.broadcastScheduler != null) {
            this.broadcastScheduler.cancel();
        }
        PartyAPI partyAPI = RDuels.beanScope().get(PartyAPI.class);
        plugin.getServer().getOnlinePlayers()
                .stream()
                .map(BukkitAdapter::adapt)
                .filter(partyAPI::isPartyPlayer)
                .forEach(player -> partyAPI.leaveParty(player, false));
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}

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
import ru.merkii.rduels.core.party.api.provider.PartyAPIProvider;
import ru.merkii.rduels.core.party.command.PartyCommand;
import ru.merkii.rduels.core.party.listener.PartyListener;

@Getter
@Singleton
public class PartyCore implements Core {

    private final Lamp<BukkitCommandActor> lamp;

    @Inject
    public PartyCore(Lamp<BukkitCommandActor> lamp) {
        this.lamp = lamp;
    }

    @Override
    public void enable(RDuels plugin) {
        lamp.register(RDuels.beanScope().get(PartyCommand.class));
        plugin.registerListeners(PartyListener.class);
    }

    @Override
    public void disable(RDuels plugin) {
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

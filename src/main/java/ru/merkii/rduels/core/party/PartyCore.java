package ru.merkii.rduels.core.party;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PartyCore implements Core {

    Lamp<BukkitCommandActor> lamp;

    @Override
    public void enable(RDuels plugin) {
        lamp.register(RDuels.beanScope().get(PartyCommand.class));
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

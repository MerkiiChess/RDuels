package ru.merkii.rduels.core.party;

import lombok.Getter;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.api.provider.PartyAPIProvider;
import ru.merkii.rduels.core.party.command.PartyCommand;
import ru.merkii.rduels.core.party.config.PartyConfig;
import ru.merkii.rduels.core.party.listener.PartyListener;

@Getter
public class PartyCore implements Core {

    public static PartyCore INSTANCE;
    private PartyConfig partyConfig;
    private PartyAPI partyAPI;

    @Override
    public void enable(RDuels plugin) {
        INSTANCE = this;
        this.partyConfig = plugin.loadSettings("partyConfig.json", PartyConfig.class);
        this.partyAPI = new PartyAPIProvider();
        plugin.registerCommands(new PartyCommand());
        plugin.registerListeners(new PartyListener());
    }

    @Override
    public void disable(RDuels plugin) {
        plugin.getServer().getOnlinePlayers().stream().filter(player -> this.partyAPI.isPartyPlayer(player)).forEach(player -> this.partyAPI.leaveParty(player, false));
    }

    @Override
    public void reloadConfig(RDuels plugin) {
        this.partyConfig = plugin.loadSettings("partyConfig.json", PartyConfig.class);
    }
}

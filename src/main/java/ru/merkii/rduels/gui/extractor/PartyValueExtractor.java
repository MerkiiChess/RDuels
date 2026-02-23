package ru.merkii.rduels.gui.extractor;

import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;

public class PartyValueExtractor implements ValueExtractor {

    private final MenuConfiguration config;
    private final PartyAPI partyAPI;

    public PartyValueExtractor(MenuConfiguration config, PartyAPI partyAPI) {
        this.config = config;
        this.partyAPI = partyAPI;
    }

    @Override
    public String extract(InventoryContext context, String text, Object modelObj) {
        if (!(modelObj instanceof PartyModel model)) return text;

        String playerList = String.join(", ", model.getPlayers().stream().map(uuid -> BukkitAdapter.getPlayer(uuid).getName()).toList());
        text = text.replace("%player_list%", playerList);
        text = text.replace("%party_owner%", BukkitAdapter.getPlayer(model.getOwner()).getName());

        boolean fighting = partyAPI.isFightParty(model);
        String status = config.messages().sub("placeholder").sub("fight-status").plainMessage(fighting ? "fighting" : "free");
        text = text.replace("%fight_status%", status);

        return text;
    }
}

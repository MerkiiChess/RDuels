package ru.merkii.rduels.gui.page;

import org.bukkit.entity.Player;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;

import java.util.List;

public class PartyListPageResolver implements PageResolver<PartyModel> {

    private final PartyAPI partyAPI;

    public PartyListPageResolver(PartyAPI partyAPI) {
        this.partyAPI = partyAPI;
    }

    @Override
    public List<PartyModel> resolve(Player player, InventoryContext context) {
        return partyAPI.getAllParty();
    }
}
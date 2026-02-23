package ru.merkii.rduels.gui.click;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.core.duel.menu.DuelChoiceKitMenu;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public class ChallengePartyClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "CHALLENGE_PARTY";
    private final MenuConfiguration config;

    public ChallengePartyClickHandler(MenuConfiguration config) {
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        PartyModel partyModel = context.require("model");
        PartyAPI partyAPI = RDuels.beanScope().get(PartyAPI.class);
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        PartyModel clickerModel = partyAPI.getPartyModelFromPlayer(duelPlayer);
        if (clickerModel == null || !clickerModel.getOwner().equals(player.getUniqueId()) || clickerModel.getOwner().equals(partyModel.getOwner()) || partyAPI.isFightParty(partyModel)) {
            return;
        }
        new DuelChoiceKitMenu().open(player, DuelRequest.create(clickerModel, partyModel), true);
        config.settings().notification().playSound(player, "challenge-party");
        config.messages().sendTo(player, Placeholder.wrapped("%party_owner%", Bukkit.getPlayer(partyModel.getOwner()).getName()), "challenge-sent");
    }
}

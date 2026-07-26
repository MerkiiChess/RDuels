package ru.merkii.rduels.core.elo.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.elo.api.EloAPI;
import ru.merkii.rduels.core.elo.config.EloConfiguration;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.statistic.StatisticService;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies match results when a fight ends: win/loss counters for every participant,
 * and Elo movement for 1v1 fights (optionally ranked-only, see elo.yml).
 */
@Singleton
public class EloListener implements Listener {

    private final EloAPI eloAPI;
    private final EloConfiguration config;
    private final StatisticService statisticService;

    @Inject
    public EloListener(EloAPI eloAPI, EloConfiguration config, StatisticService statisticService) {
        this.eloAPI = eloAPI;
        this.config = config;
        this.statisticService = statisticService;
    }

    @EventHandler
    public void onStopFight(DuelStopFightEvent event) {
        Player winner = event.getWinner();
        Player loser = event.getLoser();
        if (winner == null || loser == null) {
            // Draw / timeout / forced stop — nothing to score.
            return;
        }
        DuelFightModel fightModel = event.getDuelFightModel();

        winnerTeam(fightModel, winner).forEach(player -> statisticService.addWin(player.getUUID()));
        winnerTeam(fightModel, loser).forEach(player -> statisticService.addLoss(player.getUUID()));

        if (!config.enabled()) {
            return;
        }
        if (config.onlyRanked() && !fightModel.isRanked()) {
            return;
        }
        if (!isOneVersusOne(fightModel)) {
            return;
        }
        eloAPI.applyMatchResult(BukkitAdapter.adapt(winner), BukkitAdapter.adapt(loser));
    }

    private boolean isOneVersusOne(DuelFightModel fightModel) {
        return fightModel.getSenderParty() == null
                && fightModel.getReceiverParty() == null
                && fightModel.getPlayer2() == null
                && fightModel.getPlayer4() == null;
    }

    /** Resolves every teammate of the given player within the fight (including the player). */
    private List<DuelPlayer> winnerTeam(DuelFightModel fightModel, Player bukkitPlayer) {
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        List<DuelPlayer> team = new ArrayList<>();
        PartyModel party = partyOf(fightModel, player);
        if (party != null) {
            team.addAll(PlayerUtil.duelPlayersConvertListUUID(party.getPlayers()));
            DuelPlayer owner = BukkitAdapter.getPlayer(party.getOwner());
            if (owner != null && team.stream().noneMatch(member -> member.getUUID().equals(owner.getUUID()))) {
                team.add(owner);
            }
            return team;
        }
        team.add(player);
        DuelPlayer teammate = teammateOf(fightModel, player);
        if (teammate != null) {
            team.add(teammate);
        }
        return team;
    }

    private PartyModel partyOf(DuelFightModel fightModel, DuelPlayer player) {
        PartyModel senderParty = fightModel.getSenderParty();
        PartyModel receiverParty = fightModel.getReceiverParty();
        if (senderParty != null && (senderParty.getOwner().equals(player.getUUID()) || senderParty.getPlayers().contains(player.getUUID()))) {
            return senderParty;
        }
        if (receiverParty != null && (receiverParty.getOwner().equals(player.getUUID()) || receiverParty.getPlayers().contains(player.getUUID()))) {
            return receiverParty;
        }
        return null;
    }

    private DuelPlayer teammateOf(DuelFightModel fightModel, DuelPlayer player) {
        if (fightModel.getPlayer2() == null && fightModel.getPlayer4() == null) {
            return null;
        }
        if (player.equals(fightModel.getSender())) return fightModel.getPlayer2();
        if (player.equals(fightModel.getPlayer2())) return fightModel.getSender();
        if (player.equals(fightModel.getReceiver())) return fightModel.getPlayer4();
        if (player.equals(fightModel.getPlayer4())) return fightModel.getReceiver();
        return null;
    }

}

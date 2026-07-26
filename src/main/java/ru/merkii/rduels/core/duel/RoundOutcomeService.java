package ru.merkii.rduels.core.duel;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.event.DuelKillPlayerEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

/**
 * Programmatic round-loss trigger for non-combat outcomes (falling into void, being
 * knocked out of a sumo ring, leaving the arena). Routes through the regular
 * {@link DuelKillPlayerEvent} so rounds, stats, kill effects and streaks all behave
 * exactly as for a normal kill. Currently supports 1v1 fights only.
 */
@Singleton
public class RoundOutcomeService {

    private final DuelAPI duelAPI;

    @Inject
    public RoundOutcomeService(DuelAPI duelAPI) {
        this.duelAPI = duelAPI;
    }

    /**
     * Makes {@code victim} lose the current round of a 1v1 fight to their opponent.
     *
     * @return true if a round loss was applied
     */
    public boolean loseRoundOneVsOne(DuelPlayer victim) {
        DuelFightModel fightModel = duelAPI.getFightModelFromPlayer(victim);
        if (fightModel == null || !isOneVersusOne(fightModel)) {
            return false;
        }
        DuelPlayer killer = duelAPI.getOpponentFromFight(fightModel, victim);
        if (killer == null) {
            return false;
        }
        new DuelKillPlayerEvent(killer, victim).call();
        return true;
    }

    private boolean isOneVersusOne(DuelFightModel fightModel) {
        return fightModel.getSenderParty() == null
                && fightModel.getReceiverParty() == null
                && fightModel.getPlayer2() == null
                && fightModel.getPlayer4() == null;
    }

}

package ru.merkii.rduels.core.duel.fight;

import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.model.DuelRequest;

public interface DuelFightService {

    boolean isFightPlayer(DuelPlayer player);
    DuelFightModel getFightModelFromPlayer(DuelPlayer player);
    void startFight(DuelRequest duelRequest);
    void startFightFour(DuelPlayer p1, DuelPlayer p2, DuelPlayer p3, DuelPlayer p4, DuelRequest request);
    void nextRound(DuelFightModel duelFight);
    void stopFight(DuelFightModel duelFightModel, DuelPlayer winner, DuelPlayer loser);
    DuelPlayer getWinnerFromFight(DuelFightModel duelFightModel, DuelPlayer loser);
    DuelPlayer getLoserFromFight(DuelFightModel duelFightModel, DuelPlayer winner);
    DuelPlayer getOpponentFromFight(DuelFightModel duelFightModel, DuelPlayer player);
    DuelPlayer getOpponentFromFight(DuelPlayer player);

}

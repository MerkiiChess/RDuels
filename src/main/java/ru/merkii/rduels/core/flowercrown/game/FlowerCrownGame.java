package ru.merkii.rduels.core.flowercrown.game;

import ru.merkii.rduels.core.bedwars.game.TeamSide;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

/** Per-fight Flower Crown state: how many flowers each side has stolen. */
public class FlowerCrownGame {

    private final DuelFightModel fightModel;
    private int senderScore;
    private int receiverScore;

    public FlowerCrownGame(DuelFightModel fightModel) {
        this.fightModel = fightModel;
    }

    public DuelFightModel getFightModel() {
        return fightModel;
    }

    public int score(TeamSide side) {
        return side == TeamSide.SENDER ? senderScore : receiverScore;
    }

    public int addScore(TeamSide side) {
        if (side == TeamSide.SENDER) {
            return ++senderScore;
        }
        return ++receiverScore;
    }
}

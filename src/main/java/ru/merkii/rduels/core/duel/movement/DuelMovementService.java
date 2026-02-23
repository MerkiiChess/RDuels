package ru.merkii.rduels.core.duel.movement;

import ru.merkii.rduels.adapter.DuelPlayer;

public interface DuelMovementService {

    void addNoMove(DuelPlayer player);
    void removeNoMove(DuelPlayer player);
    boolean isNoMovePlayer(DuelPlayer player);

}

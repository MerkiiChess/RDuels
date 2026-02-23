package ru.merkii.rduels.core.duel.spectator;

import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

public interface DuelSpectatorService {

    void addSpectate(DuelPlayer player, DuelFightModel duelFightModel);
    void removeSpectate(DuelPlayer player, DuelFightModel duelFightModel, boolean fighting);
    boolean isSpectate(DuelPlayer player);
    DuelFightModel getDuelFightModelFromSpectator(DuelPlayer player);

}

package ru.merkii.rduels.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.model.PartyModel;

public interface DuelPlayer {

    @Nullable
    DuelFightModel getDuelFightModel();
    @Nullable
    PartyModel getParty();
    boolean isPartyExists();
    boolean isFight();
    boolean isQueue();
    void addKill(int amount);
    void addDeath(int amount);
    void addWin(int amount);
    int getKills();
    int getDeath();
    int getWin();
    Player getPlayer();
}

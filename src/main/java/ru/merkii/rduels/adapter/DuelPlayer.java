package ru.merkii.rduels.adapter;

import net.kyori.adventure.text.Component;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.Position;

import java.util.Optional;
import java.util.UUID;

public interface DuelPlayer {

    void sendMessage(Component component);
    void addKill();
    void addDeath();
    void addWinRound();
    void addAllRound();
    void respawnPlayer();
    void setGameMode(GameMode gameMode);
    void teleport(Position position);
    void teleport(DuelPlayer duelPlayer);
    void teleport(BlockPosition blockPosition);
    void teleport(EntityPosition entityPosition);
    void setFireTicks(long ticks);
    void setHealth(double health);
    DuelPlayer getKiller();
    String getName();
    GameMode getGameMode();
    Optional<DuelFightModel> getDuelFightModel();
    Optional<PartyModel> getParty();
    boolean isPartyExists();
    boolean isFight();
    boolean isQueue();
    int getKills();
    int getDeath();
    int getWinRounds();
    int getAllRounds();
    UUID getUUID();
    EntityPosition getEntityPosition();

}

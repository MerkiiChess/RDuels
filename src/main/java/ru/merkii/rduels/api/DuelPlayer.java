package ru.merkii.rduels.api;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.EntityPosition;
import java.util.UUID;

public interface DuelPlayer {

    void addKill();
    void addDeath();
    void addWinRound();
    void addAllRound();
    void respawnPlayer();
    void setGameMode(GameMode gameMode);
    void teleport(Location location);
    void teleport(Player player);
    void teleport(DuelPlayer duelPlayer);
    void teleport(BlockPosition blockPosition);
    void teleport(EntityPosition entityPosition);
    GameMode getGameMode();
    @Nullable
    DuelFightModel getDuelFightModel();
    @Nullable
    PartyModel getParty();
    boolean isPartyExists();
    boolean isFight();
    boolean isQueue();
    int getKills();
    int getDeath();
    int getWinRounds();
    int getAllRounds();
    Player getPlayer();
    UUID getUUID();
}

package ru.merkii.rduels.api.provider;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.api.Duel;
import ru.merkii.rduels.api.DuelPlayer;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.manager.DatabaseManager;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.UserModel;

import java.util.UUID;

public class DuelPlayerProvider implements DuelPlayer {

    private final Player player;
    private final DatabaseManager databaseManager;

    public DuelPlayerProvider(Player player) {
        this.player = player;
        this.databaseManager = RDuels.getInstance().getDatabaseManager();
    }

    @Override
    public @Nullable DuelFightModel getDuelFightModel() {
        return Duel.getDuelAPI().getFightModelFromPlayer(this.player);
    }

    @Override
    public @Nullable PartyModel getParty() {
        return Duel.getPartyAPI().getPartyModelFromPlayer(this.player);
    }

    @Override
    public boolean isPartyExists() {
        return Duel.getPartyAPI().isPartyPlayer(this.player);
    }

    @Override
    public boolean isFight() {
        return Duel.getDuelAPI().isFightPlayer(this.player);
    }

    @Override
    public boolean isQueue() {
        return Duel.getSignAPI().isQueuePlayer(this.player);
    }

    @Override
    public void addKill() {
        if (!this.databaseManager.isTableExists(this.player).join()) {
            this.databaseManager.insert(UserModel.create(this.player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        this.databaseManager.addKill(this.player).join();
    }

    @Override
    public void addDeath() {
        if (!this.databaseManager.isTableExists(this.player).join()) {
            this.databaseManager.insert(UserModel.create(this.player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        this.databaseManager.addDeath(this.player).join();
    }

    @Override
    public void addWinRound() {
        if (!this.databaseManager.isTableExists(this.player).join()) {
            this.databaseManager.insert(UserModel.create(this.player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        this.databaseManager.addWinRound(this.player).join();
    }

    @Override
    public void addAllRound() {
        if (!this.databaseManager.isTableExists(this.player).join()) {
            this.databaseManager.insert(UserModel.create(this.player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        this.databaseManager.addAllRound(this.player).join();
    }

    @Override
    public void respawnPlayer() {
        Bukkit.getScheduler().runTaskLater(RDuels.getInstance(), this.player.spigot()::respawn, 1L);
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.player.setGameMode(gameMode);
    }

    @Override
    public void teleport(Location location) {
        this.player.teleport(location);
    }

    @Override
    public void teleport(Player player) {
        this.teleport(player.getLocation());
    }

    @Override
    public void teleport(DuelPlayer duelPlayer) {
        this.teleport(duelPlayer.getPlayer().getLocation());
    }

    @Override
    public void teleport(BlockPosition blockPosition) {
        this.teleport(blockPosition.toLocation());
    }

    @Override
    public void teleport(EntityPosition entityPosition) {
        this.teleport(entityPosition.toLocation());
    }

    @Override
    public GameMode getGameMode() {
        return this.player.getGameMode();
    }

    @Override
    public int getKills() {
        return this.databaseManager.getKills(this.player).join();
    }

    @Override
    public int getDeath() {
        return this.databaseManager.getDeaths(this.player).join();
    }

    @Override
    public int getWinRounds() {
        return this.databaseManager.getWinRounds(this.player).join();
    }

    @Override
    public int getAllRounds() {
        return this.databaseManager.getAllRounds(this.player).join();
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public UUID getUUID() {
        return this.player.getUniqueId();
    }
}

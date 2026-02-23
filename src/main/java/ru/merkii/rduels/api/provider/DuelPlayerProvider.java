package ru.merkii.rduels.api.provider;

import io.avaje.inject.BeanScope;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.manager.DatabaseManager;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.Position;
import ru.merkii.rduels.model.UserModel;

import java.util.Optional;
import java.util.UUID;

public class DuelPlayerProvider implements DuelPlayer {

    private final Player player;
    private final DatabaseManager databaseManager;
    private final DuelAPI duelAPI;
    private final PartyAPI partyAPI;
    private final SignAPI signAPI;

    public DuelPlayerProvider(Player player) {
        this.player = player;
        BeanScope beanScope = RDuels.beanScope();
        this.databaseManager = beanScope.get(DatabaseManager.class);
        this.duelAPI = beanScope.get(DuelAPI.class);
        this.partyAPI = beanScope.get(PartyAPI.class);
        this.signAPI = beanScope.get(SignAPI.class);
    }

    @Override
    public Optional<DuelFightModel> getDuelFightModel() {
        return Optional.ofNullable(duelAPI.getFightModelFromPlayer(this));
    }

    @Override
    public Optional<PartyModel> getParty() {
        return Optional.ofNullable(partyAPI.getPartyModelFromPlayer(this));
    }

    @Override
    public boolean isPartyExists() {
        return partyAPI.isPartyPlayer(this);
    }

    @Override
    public boolean isFight() {
        return duelAPI.isFightPlayer(this);
    }

    @Override
    public boolean isQueue() {
        return signAPI.isQueuePlayer(this);
    }

    @Override
    public void sendMessage(Component component) {
        player.sendMessage(component);
    }

    @Override
    public void addKill() {
        if (!this.databaseManager.isTableExists(getUUID()).join()) {
            this.databaseManager.insert(UserModel.create(getUUID().toString()));
        }
        this.databaseManager.addKill(getUUID()).join();
    }

    @Override
    public void addDeath() {
        if (!this.databaseManager.isTableExists(getUUID()).join()) {
            this.databaseManager.insert(UserModel.create(getUUID().toString()));
        }
        this.databaseManager.addDeath(getUUID()).join();
    }

    @Override
    public void addWinRound() {
        if (!this.databaseManager.isTableExists(getUUID()).join()) {
            this.databaseManager.insert(UserModel.create(getUUID().toString()));
        }
        this.databaseManager.addWinRound(getUUID()).join();
    }

    @Override
    public void addAllRound() {
        if (!this.databaseManager.isTableExists(getUUID()).join()) {
            this.databaseManager.insert(UserModel.create(getUUID().toString()));
        }
        this.databaseManager.addAllRound(getUUID()).join();
    }

    @Override
    public void respawnPlayer() {
        Bukkit.getScheduler().runTaskLater(RDuels.getInstance(), this.player.spigot()::respawn, 1L);
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        player.setGameMode(BukkitAdapter.adapt(gameMode));
    }

    @Override
    public void teleport(Position position) {
        this.teleportTo(position);
    }

    @Override
    public void teleport(DuelPlayer duelPlayer) {
        this.teleportTo(duelPlayer.getEntityPosition());
    }

    @Override
    public void teleport(BlockPosition blockPosition) {
        this.teleportTo(blockPosition);
    }

    @Override
    public void teleport(EntityPosition entityPosition) {
        this.teleportTo(entityPosition);
    }

    @Override
    public void setFireTicks(long ticks) {
        player.setFireTicks((int)ticks);
    }

    @Override
    public void setHealth(double health) {
        player.setHealth(health);
    }

    @Override
    public DuelPlayer getKiller() {
        return BukkitAdapter.adapt(player.getKiller());
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.valueOf(player.getGameMode().name());
    }

    @Override
    public int getKills() {
        return this.databaseManager.getKills(getUUID()).join();
    }

    @Override
    public int getDeath() {
        return this.databaseManager.getDeaths(getUUID()).join();
    }

    @Override
    public int getWinRounds() {
        return this.databaseManager.getWinRounds(getUUID()).join();
    }

    @Override
    public int getAllRounds() {
        return this.databaseManager.getAllRounds(getUUID()).join();
    }

    @Override
    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    @Override
    public EntityPosition getEntityPosition() {
        return BukkitAdapter.adapt(player.getLocation());
    }

    private void teleportTo(Position position) {
        Location loc = BukkitAdapter.adapt(position);
        player.teleport(loc);
    }
}

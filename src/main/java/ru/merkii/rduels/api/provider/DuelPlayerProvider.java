package ru.merkii.rduels.api.provider;

import io.avaje.inject.BeanScope;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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

import java.util.Optional;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DuelPlayerProvider implements DuelPlayer {

    Player player;
    DatabaseManager databaseManager;
    DuelAPI duelAPI;
    PartyAPI partyAPI;
    SignAPI signAPI;

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
        databaseManager.addKill(getUUID());
    }

    @Override
    public void addDeath() {
        databaseManager.addDeath(getUUID());
    }

    @Override
    public void addWinRound() {
        databaseManager.addWinRound(getUUID());
    }

    @Override
    public void addAllRound() {
        databaseManager.addAllRound(getUUID());
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
        return databaseManager.getKills(getUUID());
    }

    @Override
    public int getDeath() {
        return databaseManager.getDeaths(getUUID());
    }

    @Override
    public int getWinRounds() {
        return databaseManager.getWinRounds(getUUID());
    }

    @Override
    public int getAllRounds() {
        return databaseManager.getAllRounds(getUUID());
    }

    @Override
    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    @Override
    public EntityPosition getEntityPosition() {
        return BukkitAdapter.adapt(player.getLocation());
    }

    @Override
    public boolean isOnline() {
        return player != null && player.isOnline();
    }

    private void teleportTo(Position position) {
        Location loc = BukkitAdapter.adapt(position);
        player.teleport(loc);
    }
}

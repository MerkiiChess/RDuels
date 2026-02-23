package ru.merkii.rduels.manager;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import ru.merkii.rduels.database.sql.Executor;
import ru.merkii.rduels.model.UserModel;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
public class DatabaseManager {

    private final Executor executor;

    @Inject
    public DatabaseManager(Executor executor) {
        this.executor = executor;
    }

    public CompletableFuture<Void> createTable() {
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> insert(UserModel userModel) {
        return CompletableFuture.runAsync(() -> this.executor.insert(userModel));
    }

    public CompletableFuture<Void> setDay(UUID uuid) {
        return CompletableFuture.runAsync(() -> this.executor.setDay(uuid));
    }

    public CompletableFuture<Void> setNight(UUID uuid) {
        return CompletableFuture.runAsync(() -> this.executor.setNight(uuid));
    }

    public CompletableFuture<Void> addKill(UUID uuid) {
        return CompletableFuture.runAsync(() -> this.executor.addKill(uuid));
    }

    public CompletableFuture<Void> addDeath(UUID uuid) {
        return CompletableFuture.runAsync(() -> this.executor.addDeath(uuid));
    }

    public CompletableFuture<Void> addWinRound(UUID uuid) {
        return CompletableFuture.runAsync(() -> this.executor.addWinRound(uuid));
    }

    public CompletableFuture<Void> addAllRound(UUID uuid) {
        return CompletableFuture.runAsync(() -> this.executor.addAllRounds(uuid));
    }

    public CompletableFuture<Integer> getKills(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.executor.getKills(uuid));
    }

    public CompletableFuture<Integer> getDeaths(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.executor.getDeaths(uuid));
    }

    public CompletableFuture<Integer> getWinRounds(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.executor.getWinRounds(uuid));
    }

    public CompletableFuture<Integer> getAllRounds(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.executor.getAllRounds(uuid));
    }

    public CompletableFuture<Boolean> isDay(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.executor.isDay(uuid));
    }

    public CompletableFuture<Boolean> isNight(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.executor.isNight(uuid));
    }

    public CompletableFuture<Boolean> isTableExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.executor.isTableExists(uuid.toString()));
    }
}
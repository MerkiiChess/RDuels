package ru.merkii.rduels.manager;

import com.j256.ormlite.table.TableUtils;
import org.bukkit.entity.Player;
import ru.merkii.rduels.database.sql.Executor;
import ru.merkii.rduels.model.UserModel;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final Executor executor = new Executor();

    public CompletableFuture<Void> createTable() {
        return CompletableFuture.runAsync(() -> {
            try {
                TableUtils.createTableIfNotExists(executor.getConnectionSource(), UserModel.class);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> insert(UserModel userModel) {
        return CompletableFuture.runAsync(() -> this.executor.insert(userModel));
    }

    public CompletableFuture<Void> setDay(Player player) {
        return CompletableFuture.runAsync(() -> this.executor.setDay(player));
    }

    public CompletableFuture<Void> setNight(Player player) {
        return CompletableFuture.runAsync(() -> this.executor.setNight(player));
    }

    public CompletableFuture<Void> addKill(Player player) {
        return CompletableFuture.runAsync(() -> this.executor.addKill(player));
    }

    public CompletableFuture<Void> addDeath(Player player) {
        return CompletableFuture.runAsync(() -> this.executor.addDeath(player));
    }

    public CompletableFuture<Void> addWinRound(Player player) {
        return CompletableFuture.runAsync(() -> this.executor.addWinRound(player));
    }

    public CompletableFuture<Void> addAllRound(Player player) {
        return CompletableFuture.runAsync(() -> this.executor.addAllRounds(player));
    }

    public CompletableFuture<Integer> getKills(Player player) {
        return CompletableFuture.supplyAsync(() -> this.executor.getKills(player));
    }

    public CompletableFuture<Integer> getDeaths(Player player) {
        return CompletableFuture.supplyAsync(() -> this.executor.getDeaths(player));
    }

    public CompletableFuture<Integer> getWinRounds(Player player) {
        return CompletableFuture.supplyAsync(() -> this.executor.getWinRounds(player));
    }

    public CompletableFuture<Integer> getAllRounds(Player player) {
        return CompletableFuture.supplyAsync(() -> this.executor.getAllRounds(player));
    }

    public CompletableFuture<Boolean> isDay(Player player) {
        return CompletableFuture.supplyAsync(() -> this.executor.isDay(player));
    }

    public CompletableFuture<Boolean> isNight(Player player) {
        return CompletableFuture.supplyAsync(() -> this.executor.isNight(player));
    }

    public CompletableFuture<Boolean> isTableExists(Player player) {
        return CompletableFuture.supplyAsync(() -> this.executor.isTableExists(player.getUniqueId().toString()));
    }

}

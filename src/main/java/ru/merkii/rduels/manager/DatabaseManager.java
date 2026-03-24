package ru.merkii.rduels.manager;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.database.sql.Executor;
import ru.merkii.rduels.model.UserModel;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DatabaseManager {

    Map<UUID, UserModel> userCache = new ConcurrentHashMap<>();
    Executor executor;

    public CompletableFuture<Void> createTable() {
        return CompletableFuture.completedFuture(null);
    }
    public CompletableFuture<Void> loadUserData(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            UserModel model = executor.getUserModel(uuid.toString());
            if (model == null) {
                model = UserModel.create(uuid.toString());
                executor.insert(model);
            }
            userCache.put(uuid, model);
        });
    }

    public void unloadUserData(UUID uuid) {
        userCache.remove(uuid);
    }

    public UserModel getCachedUser(UUID uuid) {
        return userCache.getOrDefault(uuid, UserModel.create(uuid.toString()));
    }

    public void addKill(UUID uuid) {
        UserModel model = userCache.get(uuid);
        if (model != null) model.setKills(model.getKills() + 1);
        CompletableFuture.runAsync(() -> executor.addKill(uuid));
    }

    public void setDay(UUID uuid) {
        UserModel model = userCache.get(uuid);
        if (model != null) model.setDay(true);
        CompletableFuture.runAsync(() -> executor.setDay(uuid));
    }

    public void setNight(UUID uuid) {
        UserModel model = userCache.get(uuid);
        if (model != null) model.setNight(true);
        CompletableFuture.runAsync(() -> executor.setNight(uuid));
    }

    public void addDeath(UUID uuid) {
        UserModel model = userCache.get(uuid);
        if (model != null) model.setDeath(model.getDeath() + 1);
        CompletableFuture.runAsync(() -> executor.addDeath(uuid));
    }

    public void addWinRound(UUID uuid) {
        UserModel model = userCache.get(uuid);
        if (model != null) model.setWinRounds(model.getWinRounds() + 1);
        CompletableFuture.runAsync(() -> executor.addWinRound(uuid));
    }

    public void addAllRound(UUID uuid) {
        UserModel model = userCache.get(uuid);
        if (model != null) model.setAllRounds(model.getAllRounds() + 1);
        CompletableFuture.runAsync(() -> executor.addAllRounds(uuid));
    }

    public int getKills(UUID uuid) {
        UserModel model = userCache.get(uuid);
        return model == null ? 0 : model.getKills();
    }

    public int getDeaths(UUID uuid) {
        UserModel model = userCache.get(uuid);
        return model == null ? 0 : model.getDeath();
    }

    public int getWinRounds(UUID uuid) {
        UserModel model = userCache.get(uuid);
        return model == null ? 0 : model.getWinRounds();
    }

    public int getAllRounds(UUID uuid) {
        UserModel model = userCache.get(uuid);
        return model == null ? 0 : model.getAllRounds();
    }

    public boolean isDay(UUID uuid) {
        UserModel model = userCache.get(uuid);
        return model == null || model.isDay();
    }

    public boolean isNight(UUID uuid) {
        UserModel model = userCache.get(uuid);
        return model != null && model.isNight();
    }

    public CompletableFuture<Boolean> isTableExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.executor.isTableExists(uuid.toString()));
    }
}
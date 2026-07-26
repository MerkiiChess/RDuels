package ru.merkii.rduels.database.sql;

import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.model.UserModel;
import ru.merkii.rduels.statistic.UserStats;

@Singleton
public class Executor {

    private final Database database;

    @Inject
    public Executor(Database database) {
        this.database = database;
    }

    public void insert(UserModel userModel) {
        database.save(userModel);
    }

    /**
     * Loads the user row, creating and persisting a fresh one if it does not exist yet.
     * Always returns a non-null, managed entity.
     */
    public UserModel getOrCreate(String uuid) {
        UserModel model = getUserModel(uuid);
        if (model == null) {
            model = UserModel.create(uuid);
            insert(model);
        }
        return model;
    }

    /**
     * Persists the full player row (statistics, Elo, tier and the day/night preference)
     * using absolute values from the in-memory cache. The cache is the single source of
     * truth, so there is no read-modify-write increment race here.
     */
    public void saveStats(UserStats stats) {
        String uuid = stats.getUuid().toString();
        UserModel model = getUserModel(uuid);
        if (model == null) {
            model = UserModel.create(uuid);
        }
        if (stats.getName() != null) {
            model.setName(stats.getName());
        }
        model.setKills(stats.getKills());
        model.setDeath(stats.getDeaths());
        model.setWinRounds(stats.getWinRounds());
        model.setAllRounds(stats.getAllRounds());
        model.setElo(stats.getElo());
        model.setWins(stats.getWins());
        model.setLosses(stats.getLosses());
        model.setTier(Math.max(0, stats.getTier()));
        model.setDay(stats.isDay());
        model.setNight(stats.isNight());
        model.setScoreboardEnabled(stats.isScoreboardEnabled());
        model.setDuelRequestsEnabled(stats.isDuelRequestsEnabled());
        model.setAutoGg(stats.isAutoGg());
        model.setAutoRequeue(stats.isAutoRequeue());
        model.setKillEffect(stats.getKillEffect());
        database.save(model);
    }

    @Nullable
    public UserModel getUserModel(String UUID) {
        UserModel model = database.find(UserModel.class)
                .where()
                .eq("UUID", UUID)
                .findOne();
        return model;
    }
}
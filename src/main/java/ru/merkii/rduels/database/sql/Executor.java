package ru.merkii.rduels.database.sql;

import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.model.UserModel;

import java.util.UUID;

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

    public void addKill(UUID uuid) {
        UserModel userModel = getUserModel(uuid.toString());
        userModel.setKills(userModel.getKills() + 1);
        database.update(userModel);
    }

    public void setDay(UUID uuid) {
        UserModel userModel = getUserModel(uuid.toString());
        userModel.setDay(true);
        userModel.setNight(false);
        database.update(userModel);
    }

    public void setNight(UUID uuid) {
        UserModel userModel = getUserModel(uuid.toString());
        userModel.setNight(true);
        userModel.setDay(false);
        database.update(userModel);
    }

    public void addDeath(UUID uuid) {
        UserModel userModel = getUserModel(uuid.toString());
        userModel.setDeath(userModel.getDeath() + 1);
        database.update(userModel);
    }

    public void addWinRound(UUID uuid) {
        UserModel userModel = getUserModel(uuid.toString());
        userModel.setWinRounds(userModel.getWinRounds() + 1);
        database.update(userModel);
    }

    public void addAllRounds(UUID uuid) {
        UserModel userModel = getUserModel(uuid.toString());
        userModel.setAllRounds(userModel.getAllRounds() + 1);
        database.update(userModel);
    }

    public int getKills(UUID uuid) {
        if (!this.isTableExists(uuid.toString())) {
            this.insert(UserModel.create(uuid.toString()));
        }
        return getUserModel(uuid.toString()).getKills();
    }

    public int getDeaths(UUID uuid) {
        if (!this.isTableExists(uuid.toString())) {
            this.insert(UserModel.create(uuid.toString()));
        }
        return getUserModel(uuid.toString()).getDeath();
    }

    public int getWinRounds(UUID uuid) {
        if (!this.isTableExists(uuid.toString())) {
            this.insert(UserModel.create(uuid.toString()));
        }
        return getUserModel(uuid.toString()).getWinRounds();
    }

    public int getAllRounds(UUID uuid) {
        if (!this.isTableExists(uuid.toString())) {
            this.insert(UserModel.create(uuid.toString()));
        }
        return getUserModel(uuid.toString()).getAllRounds();
    }

    public boolean isDay(UUID uuid) {
        if (!this.isTableExists(uuid.toString())) {
            this.insert(UserModel.create(uuid.toString()));
        }
        return getUserModel(uuid.toString()).isDay();
    }

    public boolean isNight(UUID uuid) {
        if (!this.isTableExists(uuid.toString())) {
            this.insert(UserModel.create(uuid.toString()));
        }
        return getUserModel(uuid.toString()).isNight();
    }

    public boolean isTableExists(String UUID) {
        return database.find(UserModel.class)
                .where()
                .eq("UUID", UUID)
                .findCount() > 0;
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
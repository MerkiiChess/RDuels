package ru.merkii.rduels.database.sql;

import org.bukkit.entity.Player;
import ru.merkii.rduels.database.Query;
import ru.merkii.rduels.model.UserModel;

import java.sql.SQLException;

public class Executor extends Query {

    public void insert(UserModel userModel) {
        try {
            getBlackDataDao().create(userModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addKill(Player player) {
        try {
            UserModel userModel = getUserModel(player.getUniqueId().toString());
            userModel.setKills(userModel.getKills() + 1);
            getBlackDataDao().update(userModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDay(Player player) {
        try {
            UserModel userModel = getUserModel(player.getUniqueId().toString());
            userModel.setDay(true);
            userModel.setNight(false);
            getBlackDataDao().update(userModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setNight(Player player) {
        try {
            UserModel userModel = getUserModel(player.getUniqueId().toString());
            userModel.setNight(true);
            userModel.setDay(false);
            getBlackDataDao().update(userModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDeath(Player player) {
        try {
            UserModel userModel = getUserModel(player);
            userModel.setDeath(userModel.getDeath() + 1);
            getBlackDataDao().update(userModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addWinRound(Player player) {
        try {
             UserModel userModel = getUserModel(player);
             userModel.setWinRounds(userModel.getWinRounds() + 1);
             getBlackDataDao().update(userModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAllRounds(Player player) {
        try {
            UserModel userModel = getUserModel(player);
            userModel.setAllRounds(userModel.getAllRounds() + 1);
            getBlackDataDao().update(userModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getKills(Player player) {
        if (!this.isTableExists(player.getUniqueId().toString())) {
            this.insert(UserModel.create(player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        return getUserModel(player).getKills();
    }

    public int getDeaths(Player player) {
        if (!this.isTableExists(player.getUniqueId().toString())) {
            this.insert(UserModel.create(player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        return getUserModel(player).getDeath();
    }

    public int getWinRounds(Player player) {
        if (!this.isTableExists(player.getUniqueId().toString())) {
            this.insert(UserModel.create(player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        return getUserModel(player).getWinRounds();
    }

    public int getAllRounds(Player player) {
        if (!this.isTableExists(player.getUniqueId().toString())) {
            this.insert(UserModel.create(player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        return getUserModel(player).getAllRounds();
    }

    public boolean isDay(Player player) {
        if (!this.isTableExists(player.getUniqueId().toString())) {
            this.insert(UserModel.create(player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        return getUserModel(player).isDay();
    }

    public boolean isNight(Player player) {
        if (!this.isTableExists(player.getUniqueId().toString())) {
            this.insert(UserModel.create(player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        return getUserModel(player).isNight();
    }

    public boolean isTableExists(String UUID) {
        try {
            return !getBlackDataDao().queryForAll().isEmpty() && !getBlackDataDao().queryForEq("UUID", UUID).isEmpty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserModel getUserModel(Player player) {
        return getUserModel(player.getUniqueId().toString());
    }

    public UserModel getUserModel(String UUID) {
        try {
            return getBlackDataDao().queryForEq("UUID", UUID).get(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}

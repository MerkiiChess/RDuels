package ru.merkii.rduels.database.sql;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.model.UserModel;

import java.sql.*;
import java.util.UUID;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Executor {

    HikariDataSource dataSource;
    boolean isSQLite;

    public void insert(UserModel userModel) {
        String id = userModel.getUUID();
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean exists = isRowExists(conn, id);
                if (exists) {
                    String sql = "UPDATE users SET kills = ?, death = ?, winRounds = ?, allRounds = ?, day = ?, night = ? WHERE UUID = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        setUserModelParams(ps, userModel, 1);
                        ps.setString(7, id);
                        ps.executeUpdate();
                    }
                } else {
                    String sql = "INSERT INTO users (UUID, kills, death, winRounds, allRounds, day, night) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, id);
                        setUserModelParams(ps, userModel, 2);
                        ps.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUserModelParams(PreparedStatement ps, UserModel userModel, int startIndex) throws SQLException {
        ps.setInt(startIndex, userModel.getKills());
        ps.setInt(startIndex + 1, userModel.getDeath());
        ps.setInt(startIndex + 2, userModel.getWinRounds());
        ps.setInt(startIndex + 3, userModel.getAllRounds());
        ps.setInt(startIndex + 4, userModel.isDay() ? 1 : 0);
        ps.setInt(startIndex + 5, userModel.isNight() ? 1 : 0);
    }

    public void addKill(UUID uuid) {
        updateIncrement(uuid.toString(), "kills");
    }

    public void addDeath(UUID uuid) {
        updateIncrement(uuid.toString(), "death");
    }

    public void addWinRound(UUID uuid) {
        updateIncrement(uuid.toString(), "winRounds");
    }

    public void addAllRounds(UUID uuid) {
        updateIncrement(uuid.toString(), "allRounds");
    }

    private void updateIncrement(String id, String column) {
        String sql = "UPDATE users SET " + column + " = " + column + " + 1 WHERE UUID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDay(UUID uuid) {
        updateBooleans(uuid.toString(), true, false);
    }

    public void setNight(UUID uuid) {
        updateBooleans(uuid.toString(), false, true);
    }

    private void updateBooleans(String id, boolean day, boolean night) {
        String sql = "UPDATE users SET day = ?, night = ? WHERE UUID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, day ? 1 : 0);
            ps.setInt(2, night ? 1 : 0);
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getKills(UUID uuid) {
        String id = uuid.toString();
        ensureExists(id);
        return getInt(id, "kills");
    }

    public int getDeaths(UUID uuid) {
        String id = uuid.toString();
        ensureExists(id);
        return getInt(id, "death");
    }

    public int getWinRounds(UUID uuid) {
        String id = uuid.toString();
        ensureExists(id);
        return getInt(id, "winRounds");
    }

    public int getAllRounds(UUID uuid) {
        String id = uuid.toString();
        ensureExists(id);
        return getInt(id, "allRounds");
    }

    private int getInt(String id, String column) {
        String sql = "SELECT " + column + " FROM users WHERE UUID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public boolean isDay(UUID uuid) {
        String id = uuid.toString();
        ensureExists(id);
        return getBoolean(id, "day");
    }

    public boolean isNight(UUID uuid) {
        String id = uuid.toString();
        ensureExists(id);
        return getBoolean(id, "night");
    }

    private boolean getBoolean(String id, String column) {
        String sql = "SELECT " + column + " FROM users WHERE UUID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) != 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean isTableExists(String uuid) {
        return isRowExists(uuid);
    }

    private boolean isRowExists(String id) {
        try (Connection conn = dataSource.getConnection()) {
            return isRowExists(conn, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isRowExists(Connection conn, String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE UUID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @Nullable
    public UserModel getUserModel(String uuid) {
        String sql = "SELECT * FROM users WHERE UUID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserModel model = new UserModel();
                    model.setUUID(rs.getString("UUID"));
                    model.setKills(rs.getInt("kills"));
                    model.setDeath(rs.getInt("death"));
                    model.setWinRounds(rs.getInt("winRounds"));
                    model.setAllRounds(rs.getInt("allRounds"));
                    model.setDay(rs.getInt("day") != 0);
                    model.setNight(rs.getInt("night") != 0);
                    return model;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void ensureExists(String id) {
        String sql;
        if (isSQLite) {
            sql = "INSERT OR IGNORE INTO users (UUID, kills, death, winRounds, allRounds, day, night) VALUES (?, 0, 0, 0, 0, 0, 0)";
        } else {
            sql = "INSERT INTO users (UUID, kills, death, winRounds, allRounds, day, night) VALUES (?, 0, 0, 0, 0, 0, 0) ON DUPLICATE KEY UPDATE kills = kills";
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
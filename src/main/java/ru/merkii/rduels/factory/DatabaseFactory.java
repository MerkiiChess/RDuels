package ru.merkii.rduels.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Named;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.exception.PluginStartupException;
import ru.merkii.rduels.config.settings.MySQLConfiguration;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.util.PluginConsole;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

@Factory
public class DatabaseFactory {

    @Bean(autoCloseable = true)
    public HikariDataSource dataSource(RDuels plugin, SettingsConfiguration settings) {
        MySQLConfiguration sql = settings.mySql();
        String driverName = normalizeDriverName(sql.driverName());

        HikariConfig config = new HikariConfig();
        boolean isSQLite = driverName.equals("sqlite");

        if (isSQLite) {
            String fileName = sql.file();
            File dbFile = new File(plugin.getDataFolder(), fileName);
            File parent = dbFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            config.setDriverClassName(loadDriverClass("org.sqlite.JDBC", "SQLite"));
            config.setMaximumPoolSize(1);
            config.setMinimumIdle(1);
        } else {
            if (sql.url() == null || sql.url().isBlank()) {
                throw new PluginStartupException("В settings.yml не заполнен путь settings.my-sql.url для MySQL.");
            }
            config.setJdbcUrl(sql.url().trim());
            config.setDriverClassName(loadDriverClass("com.mysql.cj.jdbc.Driver", "MySQL"));
            config.setUsername(sql.user());
            config.setPassword(sql.password());
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
        }
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            HikariDataSource ds = new HikariDataSource(config);
            createTableIfNotExists(ds, isSQLite);
            PluginConsole.info(plugin, "База данных RDuels инициализирована: " + driverName + ".");
            return ds;
        } catch (RuntimeException exception) {
            throw new PluginStartupException("Не удалось инициализировать базу данных RDuels. Проверьте настройки блока my-sql в settings.yml.", exception);
        }
    }

    @Bean
    @Named("isSQLite")
    public boolean isSQLite(SettingsConfiguration sql) {
        return normalizeDriverName(sql.mySql().driverName()).equals("sqlite");
    }

    private String normalizeDriverName(String rawDriverName) {
        String driverName = rawDriverName == null ? "sqlite" : rawDriverName.trim().toLowerCase(Locale.ROOT);
        if (driverName.equals("sqlite") || driverName.equals("mysql")) {
            return driverName;
        }
        throw new PluginStartupException("settings.yml > my-sql.driver-name поддерживает только sqlite и mysql.");
    }

    private String loadDriverClass(String driverClassName, String driverDisplayName) {
        try {
            Class.forName(driverClassName);
            return driverClassName;
        } catch (ClassNotFoundException exception) {
            throw new PluginStartupException("Не найден JDBC-драйвер для " + driverDisplayName + ". Пересоберите плагин вместе с зависимостями.", exception);
        }
    }

    private void createTableIfNotExists(HikariDataSource ds, boolean sqlite) {
        String createSql = sqlite
                ? """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    UUID TEXT NOT NULL UNIQUE,
                    kills INTEGER NOT NULL DEFAULT 0,
                    death INTEGER NOT NULL DEFAULT 0,
                    winRounds INTEGER NOT NULL DEFAULT 0,
                    allRounds INTEGER NOT NULL DEFAULT 0,
                    day INTEGER NOT NULL DEFAULT 1,
                    night INTEGER NOT NULL DEFAULT 0
                );
                """
                : """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER NOT NULL AUTO_INCREMENT,
                    UUID VARCHAR(36) NOT NULL UNIQUE,
                    kills INTEGER NOT NULL DEFAULT 0,
                    death INTEGER NOT NULL DEFAULT 0,
                    winRounds INTEGER NOT NULL DEFAULT 0,
                    allRounds INTEGER NOT NULL DEFAULT 0,
                    day TINYINT(1) NOT NULL DEFAULT 1,
                    night TINYINT(1) NOT NULL DEFAULT 0,
                    PRIMARY KEY (id)
                );
                """;

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSql);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}

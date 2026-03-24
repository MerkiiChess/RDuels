package ru.merkii.rduels.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Named;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.MySQLConfiguration;
import ru.merkii.rduels.config.settings.SettingsConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Factory
public class DatabaseFactory {

    @Bean(autoCloseable = true)
    public HikariDataSource dataSource(RDuels plugin, SettingsConfiguration settings) {
        MySQLConfiguration sql = settings.mySql();
        String driverName = sql.driverName();

        HikariConfig config = new HikariConfig();
        boolean isSQLite = driverName.equalsIgnoreCase("sqlite");

        if (isSQLite) {
            String fileName = sql.file();
            File dbFile = new File(plugin.getDataFolder(), fileName);
            config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            config.setDriverClassName("org.sqlite.JDBC");
        } else {
            config.setJdbcUrl(sql.url());
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername(sql.user());
            config.setPassword(sql.password());
        }
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(config);
        createTableIfNotExists(ds);

        return ds;
    }

    @Bean
    @Named("isSQLite")
    public boolean isSQLite(SettingsConfiguration sql) {
        return sql.mySql().driverName().equalsIgnoreCase("sqlite");
    }

    private void createTableIfNotExists(HikariDataSource ds) {
        String createSql = """
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
                """;
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
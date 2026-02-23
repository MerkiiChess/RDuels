package ru.merkii.rduels.ebean;

import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceBuilder;
import io.ebean.datasource.DataSourceBuilder.Settings;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.migration.MigrationConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.merkii.rduels.util.JarUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {

    private static final Logger log = Logger.getLogger("ebean-wrapper-config");
    private static final Set<Class<?>> classes = new HashSet<>();

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        return builder()
                .driverPath(new File("lib"))
                .autoDownloadDriver(true)
                .migrations(plugin.getClass())
                .migrationTable("db_migrations_" + plugin.getName().replace("-", "_").replace(" ", "_"))
                .url(config.getString("database.url", "jdbc:h2:file:" + new File(plugin.getDataFolder(), plugin.getName() + ".db").getAbsolutePath()))
                .username(config.getString("database.username", "sa"))
                .password(config.getString("database.password", "sa"))
                .driver(config.getString("database.driver", "h2"));
    }

    private final DriverMapping driver;
    private final File driverPath;
    private final DatabaseConfig databaseConfig;
    private final MigrationConfig migrationConfig;
    private final boolean autoDownloadDriver;
    private final boolean runMigrations;
    private final boolean createAll;
    private final Class<?>[] entities;
    private final Class<?> migrationClass;
    private final String migrationPath;
    private final String migrationTable;

    public Config(DriverMapping driver, File driverPath, DatabaseConfig databaseConfig, MigrationConfig migrationConfig, boolean autoDownloadDriver,
                  boolean runMigrations, boolean createAll, Class<?>[] entities, Class<?> migrationClass, String migrationPath, String migrationTable) {
        this.driver = driver;
        this.driverPath = driverPath;
        this.databaseConfig = databaseConfig;
        this.migrationConfig = migrationConfig;
        this.autoDownloadDriver = autoDownloadDriver;
        this.runMigrations = runMigrations;
        this.createAll = createAll;
        this.entities = entities;
        this.migrationClass = migrationClass;
        this.migrationPath = migrationPath;
        this.migrationTable = migrationTable;
    }

    public DriverMapping getDriver() {
        return driver;
    }

    public File getDriverPath() {
        return driverPath;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public MigrationConfig getMigrationConfig() {
        return migrationConfig;
    }

    public boolean isAutoDownloadDriver() {
        return autoDownloadDriver;
    }

    public boolean isRunMigrations() {
        return runMigrations;
    }

    public boolean isCreateAll() {
        return createAll;
    }

    public Class<?>[] getEntities() {
        return entities;
    }

    public Class<?> getMigrationClass() {
        return migrationClass;
    }

    public String getMigrationPath() {
        return migrationPath;
    }

    public String getMigrationTable() {
        return migrationTable;
    }

    public Builder toBuilder() {
        DataSourceBuilder.Settings dataSourceConfig = databaseConfig.getDataSourceConfig();
        return new Builder(driver,
                driverPath,
                databaseConfig,
                migrationConfig,
                dataSourceConfig.getUsername(),
                dataSourceConfig.getPassword(),
                dataSourceConfig.getUrl(),
                dataSourceConfig,
                autoDownloadDriver,
                runMigrations,
                createAll,
                entities,
                migrationClass,
                migrationPath,
                migrationTable);
    }

    public static class Builder {

        private DriverMapping driver = DriverMapping.DRIVERS.get("h2");
        private File driverPath = new File("lib");
        private DatabaseConfig databaseConfig = defaultDatabaseConfig();
        private MigrationConfig migrationConfig = new MigrationConfig();
        private String username = "dummy";
        private String password = "dummy";
        private String url = "jdbc:h2:~/ebean";
        private Settings dataSource;
        private boolean autoDownloadDriver = true;
        private boolean runMigrations = false;
        private boolean createAll = true;
        private Class<?>[] entities = new Class[0];
        private Class<?> migrationClass = getClass();
        private String migrationPath = "dbmigration";
        private String migrationTable = "db_migration";
        private boolean localCache = false;

        public Builder driverPath(File driverPath) {
            this.driverPath = driverPath;
            return this;
        }

        Builder() {
        }

        public Builder(DriverMapping driver, File driverPath, DatabaseConfig databaseConfig, MigrationConfig migrationConfig, String username, String password,
                       String url, Settings dataSource, boolean autoDownloadDriver, boolean runMigrations, boolean createAll, Class<?>[] entities,
                       Class<?> migrationClass, String migrationPath, String migrationTable) {
            this.driver = driver;
            this.driverPath = driverPath;
            this.databaseConfig = databaseConfig;
            this.migrationConfig = migrationConfig;
            this.username = username;
            this.password = password;
            this.url = url;
            this.dataSource = dataSource;
            this.autoDownloadDriver = autoDownloadDriver;
            this.runMigrations = runMigrations;
            this.createAll = createAll;
            this.entities = entities;
            this.migrationClass = migrationClass;
            this.migrationPath = migrationPath;
            this.migrationTable = migrationTable;
        }

        public Builder entities(Class<?>... entities) {
            classes.addAll(Arrays.asList(entities.clone()));
            this.entities = entities;
            return this;
        }

        public Builder driver(DriverMapping driver) {
            this.driver = driver;
            return this;
        }

        public Builder createAll(boolean createAll) {
            this.createAll = createAll;
            return this;
        }

        public Builder localCache(boolean localCache) {
            this.localCache = localCache;
            return this;
        }

        public Builder driver(String driver) {
            if (!DriverMapping.DRIVERS.containsKey(driver)) {
                throw new IllegalArgumentException("Unable to find a valid driver mapping for " + driver + ". " +
                        "Use your custom driver mapping or one of the following: " +
                        String.join(",", DriverMapping.DRIVERS.keySet()));
            }
            this.driver = DriverMapping.DRIVERS.get(driver);
            return this;
        }

        public Builder migrations(Class<?> rootClass) {
            return migrationClass(rootClass).runMigrations(true);
        }

        public Builder migrationClass(Class<?> rootClass) {
            this.migrationClass = rootClass;
            return this;
        }

        public Builder runMigrations(boolean runMigrations) {
            this.runMigrations = runMigrations;
            return this;
        }

        public Config build() {

            if (dataSource == null) {
                dataSource = new DataSourceConfig()
                        .setPlatform(driver.getIdentifier())
                        .setUsername(username)
                        .setPassword(password)
                        .setUrl(url)
                        .setDriver(driver.getDriverClass());
            }

            databaseConfig.setDataSourceConfig(dataSource);
            databaseConfig.localOnlyL2Cache(localCache);
            classes.forEach(databaseConfig::addClass);

            if (runMigrations) {
                try {
                    File tempDir = Files.createTempDirectory(null).toFile();
                    File migrationDir = new File(tempDir, migrationPath);
                    JarUtil.copyFolderFromJar(migrationClass, migrationPath, tempDir, JarUtil.CopyOption.REPLACE_IF_EXIST);

                    databaseConfig.setRunMigration(false);
                    migrationConfig.setMigrationPath("filesystem:" + new File(migrationDir, driver.getIdentifier()).getAbsolutePath());
                    migrationConfig.setMetaTable(migrationTable);
                    migrationConfig.setPlatform(driver.getIdentifier());
                    migrationConfig.setDbUsername(username);
                    migrationConfig.setDbPassword(password);
                    migrationConfig.setDbUrl(url);
                } catch (IOException e) {
                    log.log(Level.WARNING, "IOException occurred during migration run", e);
                    databaseConfig.setRunMigration(false);
                }
            } else if (createAll) {
                databaseConfig.setRunMigration(false);
                databaseConfig.setDdlGenerate(true);
                databaseConfig.setDdlRun(true);
                migrationConfig.setMetaTable(migrationTable);
            }

            return new Config(driver, driverPath, databaseConfig, migrationConfig, autoDownloadDriver, runMigrations, createAll, entities, migrationClass,
                    migrationPath, migrationTable);
        }

        private DatabaseConfig defaultDatabaseConfig() {

            DatabaseConfig databaseConfig = new DatabaseConfig();

            databaseConfig.loadFromProperties();
            databaseConfig.setDefaultServer(true);
            databaseConfig.setRegister(true);

            return databaseConfig;
        }

        public Builder autoDownloadDriver(boolean autoDownloadDriver) {
            this.autoDownloadDriver = autoDownloadDriver;
            return this;
        }

        public Builder migrationTable(String migrationTable) {
            this.migrationTable = migrationTable;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

    }

}
package ru.merkii.rduels.ebean;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.ProfileLocation;
import io.ebean.migration.MigrationRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.sql.Driver;
import java.sql.DriverManager;

/**
 * The ebean wrapper takes a simplified database config and creates a new ebean database connection with it.
 * <p>
 * It also provides the {@link #downloadDriver()} method to download the driver jar into the drivers/ directory and load it into the classpath.
 * <p>
 * You can implement your own custom {@link DriverMapping} or use one of the existing default mappings from the {@link DriverMapping#DRIVERS} list.
 * <p>
 * You also have the option to use the alternate constructor that will take the native ebean config as input.
 */
public class EbeanWrapper implements AutoCloseable {

    private final Config config;
    private Database database;

    public EbeanWrapper() {
        this(Config.builder().build());
    }

    /**
     * Creates a new ebean wrapper using the simplified database config and the class loader of this class.
     * <p>
     * You can create the config with the builder from {@link Config#builder()}.
     *
     * @param config the database config that should be used
     */
    public EbeanWrapper(Config config) {

        this.config = config;
    }

    public File getDriverLocation() {

        return new File(config.getDriverPath(), config.getDriver().getIdentifier() + ".jar");
    }

    public boolean driverExists() {

        return getDriverLocation().exists();
    }

    public void downloadDriver(boolean overwrite) {

        File driverLocation = getDriverLocation();
        if (overwrite || !driverExists()) {
            DriverMapping driver = config.getDriver();
            config.getDriverPath().mkdirs();
            try (FileOutputStream fileOutputStream = new FileOutputStream(driverLocation)) {

                URL downloadURL = new URL(driver.getDownloadUrl());
                ReadableByteChannel readableByteChannel = Channels.newChannel(downloadURL.openStream());

                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Unable to download " + driver.getIdentifier() + " driver from " + driver.getDownloadUrl() + " to " + driverLocation.getAbsolutePath(),
                        e);
            }
        }
    }

    public void downloadDriver() {

        downloadDriver(false);
    }

    public Database getDatabase() {

        if (database == null) {
            return connect();
        }

        return database;
    }

    public Database connect() {

        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();

        DriverMapping driver = config.getDriver();
        File driverLocation = getDriverLocation();

        if (config.isAutoDownloadDriver()) {
            downloadDriver();
        }

        try {
            ClassLoader classLoader = new URLClassLoader(new URL[]{driverLocation.toURI().toURL()}, getClass().getClassLoader());
            Driver originalDriver = (Driver) Class.forName(driver.getDriverClass(), true, classLoader).getDeclaredConstructor().newInstance();
            DriverShim driverShim = new DriverShim(originalDriver);
            DriverManager.registerDriver(driverShim);
            config.getDatabaseConfig().getDataSourceConfig().driver(driverShim);
            Thread.currentThread().setContextClassLoader(classLoader);

            // Hacky way to fix compile-time queries
            ProfileLocation.create();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to find " + driver.getIdentifier() + " driver class " + driver.getDriverClass() + " inside " + driverLocation.getAbsolutePath(), e);
        }

        if (config.isRunMigrations())
            new MigrationRunner(config.getMigrationConfig()).run();

        database = DatabaseFactory.create(config.getDatabaseConfig());

        Thread.currentThread().setContextClassLoader(originalContextClassLoader);

        return database;
    }

    @Override
    public void close() {
        if (database != null)
            database.shutdown(true, true);
        this.database = null;
    }

}
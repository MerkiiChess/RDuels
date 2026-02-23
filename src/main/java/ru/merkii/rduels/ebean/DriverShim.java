package ru.merkii.rduels.ebean;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

class DriverShim implements Driver {

    private static final Logger log = Logger.getLogger("ebean-wrapper");
    public static final String DRIVER_NAME = "com.bivashy.bukkit.merchant.ebean.DriverShim";
    private final Driver driver;

    DriverShim(Driver driver) {
        this.driver = driver;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return this.driver.acceptsURL(url);
    }

    @Override
    public Connection connect(String url, Properties properties) throws SQLException {
        return this.driver.connect(url, properties);
    }

    @Override
    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties properties) throws SQLException {
        return this.driver.getPropertyInfo(url, properties);
    }

    @Override
    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return log;
    }

}
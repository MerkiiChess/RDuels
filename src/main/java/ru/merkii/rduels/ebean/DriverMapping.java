package ru.merkii.rduels.ebean;

import java.util.HashMap;
import java.util.Map;

public class DriverMapping {

    public static final Map<String, DriverMapping> DRIVERS = new HashMap<>();

    static {
        DRIVERS.put("h2", new DriverMapping("h2", "org.h2.Driver", "https://repo1.maven.org/maven2/com/h2database/h2/2.3.232/h2-2.3.232.jar"));
        DRIVERS.put("mysql", new DriverMapping("mysql", "com.mysql.jdbc.Driver", "https://repo1.maven.org/maven2/mysql/mysql-connector-java/9.0.0/mysql-connector-java-9.0.0.jar"));
        DRIVERS.put("postgres", new DriverMapping("postgres", "org.postgresql.Driver", "https://jdbc.postgresql.org/download/postgresql-42.7.3.jar"));
        DRIVERS.put("mariadb", new DriverMapping("mariadb", "org.mariadb.jdbc.Driver", "https://downloads.mariadb.com/Connectors/java/connector-java-3.4.1/mariadb-java-client-3.4.1.jar"));
        DRIVERS.put("sqlite", new DriverMapping("sqlite", "org.sqlite.JDBC", "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.46.1.0/sqlite-jdbc-3.46.1.0.jar"));
    }

    private final String identifier;
    private final String driverClass;
    private final String downloadUrl;

    public DriverMapping(String identifier, String driverClass, String downloadUrl) {
        this.identifier = identifier;
        this.driverClass = driverClass;
        this.downloadUrl = downloadUrl;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

}
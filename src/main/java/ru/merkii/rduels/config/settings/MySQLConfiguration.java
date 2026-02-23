package ru.merkii.rduels.config.settings;

import com.bivashy.configurate.objectmapping.ConfigInterface;

@ConfigInterface
public interface MySQLConfiguration {

    String user();

    String password();

    default String url() {
        return "jdbc:mysql://localhost:3306/rduels?autoreconnect=true&useSSL=false&serverTimezone=UTC";
    }

    default String file() {
        return "rduels.db";
    }

    default String driverName() {
        return "sqlite";
    }

}

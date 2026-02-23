package ru.merkii.rduels.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.ebean.Database;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.MySQLConfiguration;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.ebean.Config;
import ru.merkii.rduels.ebean.EbeanWrapper;
import ru.merkii.rduels.model.UserModel;

import java.io.File;

@Factory
public class DatabaseFactory {

    @Bean(autoCloseable = true)
    public EbeanWrapper ebeanWrapper(RDuels plugin, SettingsConfiguration settingsConfiguration) {
        MySQLConfiguration sql = settingsConfiguration.mySql();
        String driverName = sql.driverName();

        Config.Builder builder = Config.builder()
                .entities(UserModel.class)
                .driver(driverName)
                .autoDownloadDriver(true)
                .createAll(true)
                .runMigrations(false);

        if (driverName.equalsIgnoreCase("sqlite")) {
            String fileName = sql.file();
            File dbFile = new File(plugin.getDataFolder(), fileName);

            builder.url("jdbc:sqlite:" + dbFile.getAbsolutePath())
                    .username("sa")
                    .password("sa");
        } else {
            builder.url(sql.url())
                    .username(sql.user())
                    .password(sql.password());
        }

        return new EbeanWrapper(builder.build());
    }

    @Bean
    public Database database(EbeanWrapper wrapper) {
        return wrapper.getDatabase();
    }
}
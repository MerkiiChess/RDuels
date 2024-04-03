package ru.merkii.rduels.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.Settings;
import ru.merkii.rduels.model.UserModel;
import java.sql.SQLException;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Query {

    @Getter
    JdbcConnectionSource connectionSource;

    @Getter
    Dao<UserModel, Integer> blackDataDao;
    String host;
    String port;
    String database;
    String user;
    String password;

    public Query() {
        Settings.MySQL settings = RDuels.getInstance().getSettings().getMySQL();
        this.host = settings.getHost();
        this.port = settings.getPort();
        this.database = settings.getDatabase();
        this.user = settings.getUser();
        this.password = settings.getPassword();
        connect();
    }

    private void connect() {
        try {
            this.connectionSource = new JdbcConnectionSource("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoreconnect=true", this.user, this.password);
            this.blackDataDao = DaoManager.createDao(this.connectionSource, UserModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

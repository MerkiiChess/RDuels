package ru.merkii.rduels.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;

import java.util.UUID;

@DatabaseTable(tableName = "rduels")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class UserModel {

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String UUID;
    @DatabaseField
    String name;
    @DatabaseField
    int winRounds;
    @DatabaseField
    int allRounds;
    @DatabaseField
    int kills;
    @DatabaseField
    int death;
    @DatabaseField
    boolean day;
    @DatabaseField
    boolean night;

    public UserModel() {}

    public UserModel(String UUID, String name, int winRounds, int allRounds, int kills, int death, boolean night, boolean day) {
        this.UUID = UUID;
        this.name = name;
        this.winRounds = winRounds;
        this.allRounds = allRounds;
        this.kills = kills;
        this.death = death;
    }

    public static UserModel create(String UUID, int winRounds, int allRounds, int kills, int death) {
        return new UserModel(UUID, Bukkit.getOfflinePlayer(java.util.UUID.fromString(UUID)).getName(), winRounds, allRounds, kills, death, false, false);
    }

}

package ru.merkii.rduels.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class UserModel {

    int id;
    String UUID;
    String name;
    int winRounds;
    int allRounds;
    int kills;
    int death;
    boolean day;
    boolean night;

    public UserModel() {}

    public UserModel(String UUID, String name, int winRounds, int allRounds, int kills, int death, boolean night, boolean day) {
        this.UUID = UUID;
        this.name = name;
        this.winRounds = winRounds;
        this.allRounds = allRounds;
        this.kills = kills;
        this.death = death;
        this.night = night;
        this.day = day;
    }

    public static UserModel create(String UUID) {
        String playerName = Bukkit.getOfflinePlayer(java.util.UUID.fromString(UUID)).getName();
        if (playerName == null) playerName = "Unknown";
        return new UserModel(UUID, playerName, 0, 0, 0, 0, false, false);
    }
}
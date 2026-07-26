package ru.merkii.rduels.model;

import io.ebean.annotation.DbDefault;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rduels")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "UUID", unique = true, nullable = false)
    String UUID;

    String name;

    int winRounds;
    int allRounds;
    int kills;
    int death;

    int elo;
    int wins;
    int losses;
    int tier;

    boolean day;
    boolean night;

    @DbDefault("true")
    boolean scoreboardEnabled;
    @DbDefault("true")
    boolean duelRequestsEnabled;
    boolean autoGg;
    boolean autoRequeue;

    @DbDefault("")
    String killEffect;

    @WhenCreated
    Instant whenCreated;

    @WhenModified
    Instant whenModified;

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

    /** Starting Elo rating for freshly created players. */
    public static final int START_ELO = 1000;

    public static UserModel create(String UUID) {
        String playerName = Bukkit.getOfflinePlayer(java.util.UUID.fromString(UUID)).getName();
        if (playerName == null) playerName = "Unknown";
        UserModel model = new UserModel(UUID, playerName, 0, 0, 0, 0, false, false);
        model.setElo(START_ELO);
        model.setScoreboardEnabled(true);
        model.setDuelRequestsEnabled(true);
        model.setKillEffect("");
        return model;
    }
}
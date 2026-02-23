package ru.merkii.rduels.model;

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

    boolean day;
    boolean night;

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

    public static UserModel create(String UUID) {
        String playerName = Bukkit.getOfflinePlayer(java.util.UUID.fromString(UUID)).getName();
        if (playerName == null) playerName = "Unknown";
        return new UserModel(UUID, playerName, 0, 0, 0, 0, false, false);
    }
}
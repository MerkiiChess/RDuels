package ru.merkii.rduels.config.settings;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.model.EntityPosition;

import java.util.List;

@ConfigInterface
public interface SettingsConfiguration {

    String durationFight();

    String durationRequest();

    String durationToStart();

    int nextRoundTime();

    int stopFightTime();

    long dayTicks();

    long nightTicks();

    ItemConfiguration createCustomKit();

    ItemConfiguration fightParty();

    ItemConfiguration leaveParty();

    List<String> blackCommands();

    List<String> blackCommandsSpectator();

    List<EntityPosition> spawns();

    boolean itemOpenCustomKit();

    MySQLConfiguration mySql();

}
package ru.merkii.rduels.config.settings;

import lombok.Getter;
import org.bukkit.Material;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.model.EntityPosition;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Settings extends Config {

    private String durationFight = "10m";
    private String durationRequest = "2m";
    private String durationToStart = "5s";
    private int nextRoundTime = 3;
    private int stopFightTime = 4;
    private long dayTicks = 1000L;
    private long nightTicks = 16000L;
    private ItemBuilder createCustomKit = ItemBuilder.builder().setSlot(0).setDisplayName("Создать кит").setLore(fastList("Создание своих китов")).setMaterial("BOOK");
    private ItemBuilder fightParty = ItemBuilder.builder().setSlot(0).setDisplayName("Бой FFA").setLore("Кинуть бой другим игрокам").setMaterial(Material.BOOK);
    private ItemBuilder leaveParty = ItemBuilder.builder().setSlot(8).setDisplayName("Выйти с пати").setMaterial(Material.BARRIER);
    private List<String> blackCommands = fastList("fly");
    private List<String> blackCommandsSpectator = fastList("setspawn", "duel");
    private List<EntityPosition> spawns = new ArrayList<>();
    private boolean debug = true;
    private boolean itemOpenCustomKit = true;
    private MySQL mySQL = new MySQL();

    @Getter
    public static class MySQL {

        private String host = "localhost";
        private String port = "3306";
        private String user = "Admin";
        private String password = "12345";
        private String database = "minecraft";

    }

}

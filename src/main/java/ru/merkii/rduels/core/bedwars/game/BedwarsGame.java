package ru.merkii.rduels.core.bedwars.game;

import ru.merkii.rduels.core.bedwars.generator.Generator;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Live state of one Bedwars match: two teams, generators and per-player purchases. */
public class BedwarsGame {

    private final DuelFightModel fightModel;
    private final BedwarsTeam senderTeam;
    private final BedwarsTeam receiverTeam;
    private final List<Generator> generators = new ArrayList<>();
    private final Map<UUID, PlayerBuyState> buyStates = new ConcurrentHashMap<>();
    private int generatorTaskId = -1;
    private int healPoolCounter;

    public int tickHealPoolCounter() {
        return ++healPoolCounter;
    }

    public BedwarsGame(DuelFightModel fightModel, BedwarsTeam senderTeam, BedwarsTeam receiverTeam) {
        this.fightModel = fightModel;
        this.senderTeam = senderTeam;
        this.receiverTeam = receiverTeam;
    }

    public DuelFightModel getFightModel() {
        return fightModel;
    }

    public BedwarsTeam getSenderTeam() {
        return senderTeam;
    }

    public BedwarsTeam getReceiverTeam() {
        return receiverTeam;
    }

    public BedwarsTeam team(TeamSide side) {
        return side == TeamSide.SENDER ? senderTeam : receiverTeam;
    }

    public BedwarsTeam teamOf(UUID uuid) {
        if (senderTeam.getMembers().contains(uuid)) {
            return senderTeam;
        }
        if (receiverTeam.getMembers().contains(uuid)) {
            return receiverTeam;
        }
        return null;
    }

    public boolean bedAlive(TeamSide side) {
        return side == TeamSide.SENDER ? fightModel.isSenderBedAlive() : fightModel.isReceiverBedAlive();
    }

    public void setBedAlive(TeamSide side, boolean alive) {
        if (side == TeamSide.SENDER) {
            fightModel.setSenderBedAlive(alive);
        } else {
            fightModel.setReceiverBedAlive(alive);
        }
    }

    public List<Generator> getGenerators() {
        return generators;
    }

    public PlayerBuyState buyState(UUID uuid) {
        return buyStates.computeIfAbsent(uuid, ignored -> new PlayerBuyState());
    }

    public int getGeneratorTaskId() {
        return generatorTaskId;
    }

    public void setGeneratorTaskId(int generatorTaskId) {
        this.generatorTaskId = generatorTaskId;
    }
}

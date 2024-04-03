package ru.merkii.rduels.core.duel.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.schedualer.DuelScheduler;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.model.KitModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DuelFightModel {

    final Player sender;
    final Player receiver;
    final int numGames;
    final KitModel kitModel;
    final ArenaModel arenaModel;
    final List<UUID> spectates;
    @Setter
    DuelScheduler bukkitTask;
    @Setter
    SignModel signModel;
    @Setter
    int countNumGames = 0;
    @Setter
    PartyModel senderParty;
    @Setter
    PartyModel receiverParty;
    @Setter
    Player player2;
    @Setter
    Player player4;

    public DuelFightModel(Player sender, Player receiver, int numGames, KitModel kitModel, ArenaModel arenaModel) {
        this.sender = sender;
        this.receiver = receiver;
        this.numGames = numGames;
        this.kitModel = kitModel;
        this.arenaModel = arenaModel;
        this.spectates = new ArrayList<>();
    }

    @Nullable
    public PartyModel getSenderParty() {
        return this.senderParty;
    }

    @Nullable
    public PartyModel getReceiverParty() {
        return this.receiverParty;
    }
}

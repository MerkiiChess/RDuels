package ru.merkii.rduels.core.duel.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

public class DuelStopFightEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player sender;
    private final Player receiver;
    private final Player winner;
    private final Player loser;
    private final DuelFightModel duelFightModel;

    public DuelStopFightEvent(Player sender, Player receiver, Player winner, Player loser, DuelFightModel duelFightModel) {
        this.sender = sender;
        this.receiver = receiver;
        this.winner = winner;
        this.loser = loser;
        this.duelFightModel = duelFightModel;
    }

    public static DuelStopFightEvent create(Player sender, Player receiver, Player winner, Player loser, DuelFightModel duelFightModel) {
        return new DuelStopFightEvent(sender, receiver, winner, loser, duelFightModel);
    }

    public void call() {
        RDuels.getInstance().getServer().getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLoser() {
        return loser;
    }

    public DuelFightModel getDuelFightModel() {
        return duelFightModel;
    }

}

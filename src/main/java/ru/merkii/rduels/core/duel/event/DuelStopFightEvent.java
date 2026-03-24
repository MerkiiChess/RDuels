package ru.merkii.rduels.core.duel.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DuelStopFightEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    Player sender;
    Player receiver;
    Player winner;
    Player loser;
    DuelFightModel duelFightModel;

    public static DuelStopFightEvent create(DuelPlayer sender, DuelPlayer receiver, DuelPlayer winner, DuelPlayer loser, DuelFightModel duelFightModel) {
        Player bukkitSender = BukkitAdapter.adapt(sender);
        Player bukkitReceiver = BukkitAdapter.adapt(receiver);
        Player bukkitWinner = BukkitAdapter.adapt(winner);
        Player bukkitLoser = BukkitAdapter.adapt(loser);
        return create(bukkitSender, bukkitReceiver, bukkitWinner, bukkitLoser, duelFightModel);
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

}

package ru.merkii.rduels.core.duel.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DuelKillPlayerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    final DuelPlayer killer;
    @Getter
    final DuelPlayer victim;
    @Getter
    final DuelFightModel duelFightModel;
    boolean cancelled;

    public DuelKillPlayerEvent(DuelPlayer killer, DuelPlayer victim) {
        this.killer = killer;
        this.victim = victim;
        this.duelFightModel = killer != null
                ? killer.getDuelFightModel().orElseGet(() -> victim.getDuelFightModel().orElse(null))
                : victim.getDuelFightModel().orElse(null);
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void call() {
        RDuels.getInstance().getServer().getPluginManager().callEvent(this);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}

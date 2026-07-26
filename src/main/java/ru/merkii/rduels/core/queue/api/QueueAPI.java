package ru.merkii.rduels.core.queue.api;

import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.model.KitModel;

/**
 * Public kit-queue API. Fetch via {@code RDuels.beanScope().get(QueueAPI.class)}.
 */
public interface QueueAPI {

    /** Puts the player into the unranked or ranked queue of the given kit. */
    void joinQueue(DuelPlayer player, KitModel kitModel, boolean ranked);

    /** Removes the player from whatever queue they are in. Returns true if they were queued. */
    boolean leaveQueue(DuelPlayer player);

    boolean isInQueue(DuelPlayer player);

}

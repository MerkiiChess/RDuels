package ru.merkii.rduels.core.rematch.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.rematch.bucket.RematchBucket;

/**
 * Records the last opponent and kit for both players of a finished 1v1 fight, so they
 * can /rematch. Party and 2v2 fights are ignored (no single opponent).
 */
@Singleton
public class RematchListener implements Listener {

    private final RematchBucket rematchBucket;

    @Inject
    public RematchListener(RematchBucket rematchBucket) {
        this.rematchBucket = rematchBucket;
    }

    @EventHandler
    public void onStopFight(DuelStopFightEvent event) {
        DuelFightModel fightModel = event.getDuelFightModel();
        if (!isOneVersusOne(fightModel) || fightModel.getKitModel() == null) {
            return;
        }
        Player sender = event.getSender();
        Player receiver = event.getReceiver();
        if (sender == null || receiver == null) {
            return;
        }
        rematchBucket.record(sender.getUniqueId(), receiver.getUniqueId(), fightModel.getKitModel());
        rematchBucket.record(receiver.getUniqueId(), sender.getUniqueId(), fightModel.getKitModel());
    }

    private boolean isOneVersusOne(DuelFightModel fightModel) {
        return fightModel.getSenderParty() == null
                && fightModel.getReceiverParty() == null
                && fightModel.getPlayer2() == null
                && fightModel.getPlayer4() == null;
    }

}

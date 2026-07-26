package ru.merkii.rduels.core.queue.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.queue.api.QueueAPI;
import ru.merkii.rduels.core.queue.config.QueueConfiguration;
import ru.merkii.rduels.statistic.StatisticService;

/**
 * Re-queues participants of a finished queue match if they enabled Auto-requeue,
 * using the same kit and ranked flag as the fight they just left.
 */
@Singleton
public class AutoRequeueListener implements Listener {

    private final QueueAPI queueAPI;
    private final QueueConfiguration config;
    private final StatisticService statisticService;
    private final RDuels plugin;

    @Inject
    public AutoRequeueListener(QueueAPI queueAPI, QueueConfiguration config, StatisticService statisticService, RDuels plugin) {
        this.queueAPI = queueAPI;
        this.config = config;
        this.statisticService = statisticService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onStopFight(DuelStopFightEvent event) {
        DuelFightModel fightModel = event.getDuelFightModel();
        if (!config.enabled() || !fightModel.isFromQueue() || fightModel.getKitModel() == null) {
            return;
        }
        requeue(event.getWinner(), fightModel);
        requeue(event.getLoser(), fightModel);
    }

    private void requeue(Player bukkitPlayer, DuelFightModel fightModel) {
        if (bukkitPlayer == null || !statisticService.isAutoRequeue(bukkitPlayer.getUniqueId())) {
            return;
        }
        // Defer so the player is fully out of the fight (teleported back, inventory reset)
        // before re-entering the queue.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player online = Bukkit.getPlayer(bukkitPlayer.getUniqueId());
            if (online == null) {
                return;
            }
            DuelPlayer duelPlayer = BukkitAdapter.adapt(online);
            queueAPI.joinQueue(duelPlayer, fightModel.getKitModel(), fightModel.isRanked());
        }, 20L);
    }

}

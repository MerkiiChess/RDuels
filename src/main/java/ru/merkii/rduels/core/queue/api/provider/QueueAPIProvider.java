package ru.merkii.rduels.core.queue.api.provider;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.queue.api.QueueAPI;
import ru.merkii.rduels.core.queue.bucket.QueueBucket;
import ru.merkii.rduels.core.queue.model.QueueEntry;
import ru.merkii.rduels.model.KitModel;
import ru.merkii.rduels.statistic.StatisticService;

@Singleton
public class QueueAPIProvider implements QueueAPI {

    private final QueueBucket queueBucket;
    private final DuelAPI duelAPI;
    private final StatisticService statisticService;
    private final MessageConfig messages;

    @Inject
    public QueueAPIProvider(QueueBucket queueBucket, DuelAPI duelAPI, StatisticService statisticService, MessageConfig messages) {
        this.queueBucket = queueBucket;
        this.duelAPI = duelAPI;
        this.statisticService = statisticService;
        this.messages = messages;
    }

    @Override
    public void joinQueue(DuelPlayer player, KitModel kitModel, boolean ranked) {
        if (duelAPI.isFightPlayer(player)) {
            messages.sendTo(player, "queue-in-fight");
            return;
        }
        if (isInQueue(player)) {
            messages.sendTo(player, "queue-already");
            return;
        }
        queueBucket.add(new QueueEntry(player.getUUID(), kitModel, ranked, statisticService.getElo(player.getUUID())));
        messages.sendTo(player, Placeholder.Placeholders.of(
                Placeholder.of("(kit)", kitModel.getDisplayName()),
                Placeholder.of("(mode)", messages.plainMessage(ranked ? "queue-mode-ranked" : "queue-mode-unranked"))
        ), "queue-joined");
    }

    @Override
    public boolean leaveQueue(DuelPlayer player) {
        boolean removed = queueBucket.remove(player.getUUID());
        if (removed) {
            messages.sendTo(player, "queue-left");
        }
        return removed;
    }

    @Override
    public boolean isInQueue(DuelPlayer player) {
        return queueBucket.findByPlayer(player.getUUID()).isPresent();
    }

}

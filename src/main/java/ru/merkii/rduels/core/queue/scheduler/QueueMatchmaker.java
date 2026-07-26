package ru.merkii.rduels.core.queue.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.queue.bucket.QueueBucket;
import ru.merkii.rduels.core.queue.config.QueueConfiguration;
import ru.merkii.rduels.core.queue.model.QueueEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Pairs queued players into fights. Unranked queues match FIFO per kit; ranked queues
 * match players whose Elo difference fits a window that widens the longer they wait.
 */
public class QueueMatchmaker extends BukkitRunnable {

    private final QueueBucket queueBucket;
    private final QueueConfiguration config;
    private final DuelAPI duelAPI;
    private final MessageConfig messages;

    private QueueMatchmaker(QueueBucket queueBucket, QueueConfiguration config, DuelAPI duelAPI, MessageConfig messages) {
        this.queueBucket = queueBucket;
        this.config = config;
        this.duelAPI = duelAPI;
        this.messages = messages;
    }

    public static QueueMatchmaker start(RDuels plugin, QueueBucket bucket, QueueConfiguration config, DuelAPI duelAPI, MessageConfig messages) {
        QueueMatchmaker matchmaker = new QueueMatchmaker(bucket, config, duelAPI, messages);
        long interval = Math.max(1, config.matchIntervalTicks());
        matchmaker.runTaskTimer(plugin, interval, interval);
        return matchmaker;
    }

    @Override
    public void run() {
        // Drop entries of players who went offline.
        queueBucket.getEntries().removeIf(entry -> Bukkit.getPlayer(entry.getPlayer()) == null);

        List<QueueEntry> snapshot = new ArrayList<>(queueBucket.getEntries());
        List<QueueEntry> matched = new ArrayList<>();

        for (QueueEntry first : snapshot) {
            if (matched.contains(first)) {
                continue;
            }
            QueueEntry second = findOpponent(snapshot, matched, first);
            if (second == null) {
                continue;
            }
            matched.add(first);
            matched.add(second);
            startMatch(first, second);
        }
    }

    private QueueEntry findOpponent(List<QueueEntry> snapshot, List<QueueEntry> matched, QueueEntry first) {
        for (QueueEntry candidate : snapshot) {
            if (candidate == first || matched.contains(candidate)) {
                continue;
            }
            if (candidate.isRanked() != first.isRanked()) {
                continue;
            }
            if (!candidate.getKitModel().getDisplayName().equals(first.getKitModel().getDisplayName())) {
                continue;
            }
            if (first.isRanked() && !fitsEloWindow(first, candidate)) {
                continue;
            }
            return candidate;
        }
        return null;
    }

    private boolean fitsEloWindow(QueueEntry first, QueueEntry second) {
        long minWait = Math.min(first.waitSeconds(), second.waitSeconds());
        long allowed = config.rankedEloRange() + config.rankedEloRangePerSecond() * minWait;
        return Math.abs(first.getEloAtJoin() - second.getEloAtJoin()) <= allowed;
    }

    private void startMatch(QueueEntry first, QueueEntry second) {
        queueBucket.remove(first.getPlayer());
        queueBucket.remove(second.getPlayer());

        DuelPlayer sender = BukkitAdapter.getPlayer(first.getPlayer());
        DuelPlayer receiver = BukkitAdapter.getPlayer(second.getPlayer());
        if (sender == null || receiver == null) {
            return;
        }

        DuelRequest request = DuelRequest.create(sender, receiver);
        request.setKitModel(first.getKitModel());
        request.setNumGames(Math.max(1, config.numGames()));
        request.setRanked(first.isRanked());
        request.setFromQueue(true);
        // No arena in the request: startFight picks any free arena.

        messages.sendTo(sender, Placeholder.Placeholders.of(
                Placeholder.of("(kit)", first.getKitModel().getDisplayName()),
                Placeholder.of("(opponent)", receiver.getName())
        ), "queue-match-found");
        messages.sendTo(receiver, Placeholder.Placeholders.of(
                Placeholder.of("(kit)", first.getKitModel().getDisplayName()),
                Placeholder.of("(opponent)", sender.getName())
        ), "queue-match-found");

        duelAPI.startFight(request);
    }

}

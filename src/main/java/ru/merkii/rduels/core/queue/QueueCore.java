package ru.merkii.rduels.core.queue;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.queue.bucket.QueueBucket;
import ru.merkii.rduels.core.queue.command.QueueGUICommand;
import ru.merkii.rduels.core.queue.config.QueueConfiguration;
import ru.merkii.rduels.core.queue.scheduler.QueueMatchmaker;

/**
 * Kit-queue module: /queue menu, unranked and ranked queues per kit, and the
 * matchmaker pairing waiting players into fights.
 */
@Singleton
public class QueueCore implements Core {

    private final Lamp<BukkitCommandActor> lamp;
    private final QueueGUICommand queueCommand;
    private final QueueBucket queueBucket;
    private final QueueConfiguration config;
    private final DuelAPI duelAPI;
    private final MessageConfig messages;
    private QueueMatchmaker matchmaker;

    @Inject
    public QueueCore(Lamp<BukkitCommandActor> lamp, QueueGUICommand queueCommand, QueueBucket queueBucket,
                     QueueConfiguration config, DuelAPI duelAPI, MessageConfig messages) {
        this.lamp = lamp;
        this.queueCommand = queueCommand;
        this.queueBucket = queueBucket;
        this.config = config;
        this.duelAPI = duelAPI;
        this.messages = messages;
    }

    @Override
    public void enable(RDuels plugin) {
        if (!config.enabled()) {
            return;
        }
        lamp.register(queueCommand);
        this.matchmaker = QueueMatchmaker.start(plugin, queueBucket, config, duelAPI, messages);
    }

    @Override
    public void disable(RDuels plugin) {
        if (this.matchmaker != null) {
            this.matchmaker.cancel();
        }
        this.queueBucket.clear();
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}

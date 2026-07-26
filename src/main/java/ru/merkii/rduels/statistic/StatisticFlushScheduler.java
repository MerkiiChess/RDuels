package ru.merkii.rduels.statistic;

import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;

/**
 * Periodically persists dirty statistic entries so that a crash never loses more than
 * one interval's worth of stats. The actual DB writes are performed asynchronously by
 * {@link StatisticService#flushAll()}.
 */
public class StatisticFlushScheduler extends BukkitRunnable {

    private static final long INTERVAL_TICKS = 20L * 60L; // once a minute

    private final StatisticService statisticService;

    public StatisticFlushScheduler(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    public void start(RDuels plugin) {
        runTaskTimer(plugin, INTERVAL_TICKS, INTERVAL_TICKS);
    }

    @Override
    public void run() {
        statisticService.flushAll();
    }
}

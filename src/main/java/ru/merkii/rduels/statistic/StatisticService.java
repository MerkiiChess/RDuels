package ru.merkii.rduels.statistic;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.merkii.rduels.database.sql.Executor;
import ru.merkii.rduels.model.UserModel;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory, non-blocking cache for player duel statistics.
 * <p>
 * Reads ({@link #getKills} etc.) never touch the database — they return the cached
 * value, so they are safe to call from the main thread (GUIs, PlaceholderAPI, chat).
 * Writes ({@link #addKill} etc.) update the cache instantly and mark the entry dirty;
 * the actual DB persistence happens asynchronously via {@link #flush}/{@link #flushAll}
 * (driven by a repeating task) and on player quit / plugin disable.
 * <p>
 * The cache is the single source of truth while a player is online, which removes the
 * previous read-modify-write increment race in the SQL executor entirely.
 */
@Singleton
public class StatisticService {

    private final Executor executor;
    private final Map<UUID, UserStats> cache = new ConcurrentHashMap<>();

    @Inject
    public StatisticService(Executor executor) {
        this.executor = executor;
    }

    /**
     * Ensures a cache entry exists and kicks off its (idempotent) base load.
     * Call this on player join so stats are warm before the first read/increment.
     */
    public CompletableFuture<Void> load(UUID uuid) {
        return ensureLoaded(stats(uuid));
    }

    public int getKills(UUID uuid) {
        return stats(uuid).getKills();
    }

    public int getDeaths(UUID uuid) {
        return stats(uuid).getDeaths();
    }

    public int getWinRounds(UUID uuid) {
        return stats(uuid).getWinRounds();
    }

    public int getAllRounds(UUID uuid) {
        return stats(uuid).getAllRounds();
    }

    public void addKill(UUID uuid) {
        stats(uuid).addKill();
    }

    public void addDeath(UUID uuid) {
        stats(uuid).addDeath();
    }

    public void addWinRound(UUID uuid) {
        stats(uuid).addWinRound();
    }

    public void addAllRound(UUID uuid) {
        stats(uuid).addAllRound();
    }

    public int getElo(UUID uuid) {
        return stats(uuid).getElo();
    }

    public int getWins(UUID uuid) {
        return stats(uuid).getWins();
    }

    public int getLosses(UUID uuid) {
        return stats(uuid).getLosses();
    }

    public int getTier(UUID uuid) {
        return stats(uuid).getTier();
    }

    /** Applies an Elo delta (may be negative) and returns the new rating. */
    public int addElo(UUID uuid, int delta) {
        return stats(uuid).addElo(delta);
    }

    public void addWin(UUID uuid) {
        stats(uuid).addWin();
    }

    public void addLoss(UUID uuid) {
        stats(uuid).addLoss();
    }

    /** Raises the reached reward tier (never lowers it). */
    public void setTierAtLeast(UUID uuid, int tier) {
        stats(uuid).setTierAtLeast(tier);
    }

    public boolean isScoreboardEnabled(UUID uuid) {
        return stats(uuid).isScoreboardEnabled();
    }

    public boolean isDuelRequestsEnabled(UUID uuid) {
        return stats(uuid).isDuelRequestsEnabled();
    }

    public boolean isAutoGg(UUID uuid) {
        return stats(uuid).isAutoGg();
    }

    public void setScoreboardEnabled(UUID uuid, boolean value) {
        stats(uuid).setScoreboardEnabled(value);
    }

    public void setDuelRequestsEnabled(UUID uuid, boolean value) {
        stats(uuid).setDuelRequestsEnabled(value);
    }

    public void setAutoGg(UUID uuid, boolean value) {
        stats(uuid).setAutoGg(value);
    }

    public boolean isAutoRequeue(UUID uuid) {
        return stats(uuid).isAutoRequeue();
    }

    public void setAutoRequeue(UUID uuid, boolean value) {
        stats(uuid).setAutoRequeue(value);
    }

    public String getKillEffect(UUID uuid) {
        return stats(uuid).getKillEffect();
    }

    public void setKillEffect(UUID uuid, String effectId) {
        stats(uuid).setKillEffect(effectId);
    }

    public boolean isDay(UUID uuid) {
        return stats(uuid).isDay();
    }

    public boolean isNight(UUID uuid) {
        return stats(uuid).isNight();
    }

    public void setDay(UUID uuid) {
        stats(uuid).setDay();
    }

    public void setNight(UUID uuid) {
        stats(uuid).setNight();
    }

    /** Asynchronously persists every dirty entry. Safe to call on the main thread. */
    public void flushAll() {
        for (UserStats stats : cache.values()) {
            flush(stats);
        }
    }

    /** Persists a player's stats and drops them from the cache (used on quit). */
    public CompletableFuture<Void> flushAndUnload(UUID uuid) {
        UserStats stats = cache.get(uuid);
        if (stats == null) {
            return CompletableFuture.completedFuture(null);
        }
        return flush(stats).whenComplete((ignored, throwable) -> cache.remove(uuid, stats));
    }

    /**
     * Synchronously persists every dirty entry. Intended for {@code onDisable}, where we
     * must block until all data is written and the async pool may not be running.
     */
    public void flushAllBlocking() {
        for (UserStats stats : cache.values()) {
            ensureLoaded(stats).join();
            if (stats.isDirty()) {
                stats.clearDirty();
                persist(stats);
            }
        }
        cache.clear();
    }

    private CompletableFuture<Void> flush(UserStats stats) {
        if (!stats.isDirty()) {
            return CompletableFuture.completedFuture(null);
        }
        // Wait for the base to be applied before persisting, otherwise we would write
        // only the pending deltas as absolute values and clobber the stored totals.
        return ensureLoaded(stats).thenRunAsync(() -> {
            if (stats.isDirty()) {
                stats.clearDirty();
                persist(stats);
            }
        });
    }

    private void persist(UserStats stats) {
        executor.saveStats(stats);
    }

    private UserStats stats(UUID uuid) {
        UserStats stats = cache.computeIfAbsent(uuid, UserStats::new);
        ensureLoaded(stats);
        return stats;
    }

    /** Applies the persisted base exactly once, sharing a single async load future. */
    CompletableFuture<Void> ensureLoaded(UserStats stats) {
        CompletableFuture<Void> future = stats.loadFuture;
        if (future != null) {
            return future;
        }
        synchronized (stats) {
            if (stats.loadFuture != null) {
                return stats.loadFuture;
            }
            stats.loadFuture = CompletableFuture.runAsync(() -> {
                UserModel model = executor.getOrCreate(stats.getUuid().toString());
                stats.applyBase(
                        model.getKills(),
                        model.getDeath(),
                        model.getWinRounds(),
                        model.getAllRounds(),
                        model.getElo(),
                        model.getWins(),
                        model.getLosses(),
                        model.getTier(),
                        model.isDay(),
                        model.isNight(),
                        model.isScoreboardEnabled(),
                        model.isDuelRequestsEnabled(),
                        model.isAutoGg(),
                        model.isAutoRequeue(),
                        model.getKillEffect(),
                        model.getName()
                );
            });
            return stats.loadFuture;
        }
    }
}

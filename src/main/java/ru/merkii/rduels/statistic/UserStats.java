package ru.merkii.rduels.statistic;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory holder of a single player's duel statistics.
 * <p>
 * The counters are {@link AtomicInteger}s so that increments performed on the main
 * thread and the one-time base value applied by the async DB load never lose updates.
 * The persisted DB value is added on top of whatever increments already happened
 * before the load finished (see {@link StatisticService#ensureLoaded}), which is why
 * the load applies the base via {@code addAndGet} instead of overwriting.
 */
public class UserStats {

    private final UUID uuid;
    private final AtomicInteger kills = new AtomicInteger();
    private final AtomicInteger deaths = new AtomicInteger();
    private final AtomicInteger winRounds = new AtomicInteger();
    private final AtomicInteger allRounds = new AtomicInteger();
    /** Elo is stored as an additive delta until the base loads, same as the counters. */
    private final AtomicInteger elo = new AtomicInteger();
    private final AtomicInteger wins = new AtomicInteger();
    private final AtomicInteger losses = new AtomicInteger();
    /** Highest reached reward tier index; merges with the stored value via max, not addition. */
    private final AtomicInteger tier = new AtomicInteger(-1);

    private volatile String name;
    private volatile boolean dirty;

    private volatile boolean day;
    private volatile boolean night;
    /** Set once the player changes their day/night preference, so a late base-load won't clobber it. */
    private volatile boolean dayNightOverridden;

    private volatile boolean scoreboardEnabled = true;
    private volatile boolean duelRequestsEnabled = true;
    private volatile boolean autoGg;
    private volatile boolean autoRequeue;
    /** Set once the player toggles any preference, so a late base-load won't clobber it. */
    private volatile boolean togglesOverridden;

    private volatile String killEffect = "";
    private volatile boolean killEffectOverridden;

    /** Shared future guarding the one-time base-load from the database. */
    volatile CompletableFuture<Void> loadFuture;

    public UserStats(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public boolean isDirty() {
        return dirty;
    }

    void markDirty() {
        this.dirty = true;
    }

    void clearDirty() {
        this.dirty = false;
    }

    public int getKills() {
        return kills.get();
    }

    public int getDeaths() {
        return deaths.get();
    }

    public int getWinRounds() {
        return winRounds.get();
    }

    public int getAllRounds() {
        return allRounds.get();
    }

    public int getElo() {
        return elo.get();
    }

    public int getWins() {
        return wins.get();
    }

    public int getLosses() {
        return losses.get();
    }

    /** -1 means "no tier data yet" (base not loaded and no tier reached this session). */
    public int getTier() {
        return tier.get();
    }

    /** Applies an Elo delta (may be negative) and returns the new value. */
    int addElo(int delta) {
        int result = elo.addAndGet(delta);
        markDirty();
        return result;
    }

    void addWin() {
        wins.incrementAndGet();
        markDirty();
    }

    void addLoss() {
        losses.incrementAndGet();
        markDirty();
    }

    /** Raises the reached tier (never lowers it). */
    void setTierAtLeast(int newTier) {
        tier.accumulateAndGet(newTier, Math::max);
        markDirty();
    }

    public boolean isScoreboardEnabled() {
        return scoreboardEnabled;
    }

    public boolean isDuelRequestsEnabled() {
        return duelRequestsEnabled;
    }

    public boolean isAutoGg() {
        return autoGg;
    }

    public boolean isAutoRequeue() {
        return autoRequeue;
    }

    void setScoreboardEnabled(boolean value) {
        this.scoreboardEnabled = value;
        this.togglesOverridden = true;
        markDirty();
    }

    void setDuelRequestsEnabled(boolean value) {
        this.duelRequestsEnabled = value;
        this.togglesOverridden = true;
        markDirty();
    }

    void setAutoGg(boolean value) {
        this.autoGg = value;
        this.togglesOverridden = true;
        markDirty();
    }

    void setAutoRequeue(boolean value) {
        this.autoRequeue = value;
        this.togglesOverridden = true;
        markDirty();
    }

    public String getKillEffect() {
        return killEffect;
    }

    void setKillEffect(String value) {
        this.killEffect = value == null ? "" : value;
        this.killEffectOverridden = true;
        markDirty();
    }

    public boolean isDay() {
        return day;
    }

    public boolean isNight() {
        return night;
    }

    void setDay() {
        this.day = true;
        this.night = false;
        this.dayNightOverridden = true;
        markDirty();
    }

    void setNight() {
        this.night = true;
        this.day = false;
        this.dayNightOverridden = true;
        markDirty();
    }

    void addKill() {
        kills.incrementAndGet();
        markDirty();
    }

    void addDeath() {
        deaths.incrementAndGet();
        markDirty();
    }

    void addWinRound() {
        winRounds.incrementAndGet();
        markDirty();
    }

    void addAllRound() {
        allRounds.incrementAndGet();
        markDirty();
    }

    /** Applies the persisted base values exactly once, on top of any pending increments. */
    void applyBase(int kills, int deaths, int winRounds, int allRounds,
                   int elo, int wins, int losses, int tier,
                   boolean day, boolean night,
                   boolean scoreboardEnabled, boolean duelRequestsEnabled, boolean autoGg, boolean autoRequeue,
                   String killEffect, String name) {
        this.kills.addAndGet(kills);
        this.deaths.addAndGet(deaths);
        this.winRounds.addAndGet(winRounds);
        this.allRounds.addAndGet(allRounds);
        this.elo.addAndGet(elo);
        this.wins.addAndGet(wins);
        this.losses.addAndGet(losses);
        this.tier.accumulateAndGet(tier, Math::max);
        // Counters merge additively; the day/night preference is a plain flag, so only take
        // the stored value if the player hasn't already changed it during the load window.
        if (!dayNightOverridden) {
            this.day = day;
            this.night = night;
        }
        if (!togglesOverridden) {
            this.scoreboardEnabled = scoreboardEnabled;
            this.duelRequestsEnabled = duelRequestsEnabled;
            this.autoGg = autoGg;
            this.autoRequeue = autoRequeue;
        }
        if (!killEffectOverridden && killEffect != null) {
            this.killEffect = killEffect;
        }
        if (name != null) {
            this.name = name;
        }
    }
}

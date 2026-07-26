package ru.merkii.rduels.core.tnttag;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.tnttag.bucket.TntTagGameBucket;
import ru.merkii.rduels.core.tnttag.game.TntTagGame;

import java.util.ArrayList;

/**
 * TNT Tag module: pass-the-bomb elimination on tnt-tag-flagged arenas.
 * Game logic lives in the auto-registered listener.
 */
@Singleton
public class TntTagCore implements Core {

    private final TntTagGameBucket gameBucket;

    @Inject
    public TntTagCore(TntTagGameBucket gameBucket) {
        this.gameBucket = gameBucket;
    }

    @Override
    public void enable(RDuels plugin) {
    }

    @Override
    public void disable(RDuels plugin) {
        for (TntTagGame game : new ArrayList<>(gameBucket.all())) {
            if (game.getTaskId() != -1) {
                plugin.getServer().getScheduler().cancelTask(game.getTaskId());
            }
            gameBucket.remove(game);
        }
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}

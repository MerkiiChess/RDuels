package ru.merkii.rduels.core.bedwars.bucket;

import jakarta.inject.Singleton;
import ru.merkii.rduels.core.bedwars.game.BedwarsGame;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/** Registry of active Bedwars games. */
@Singleton
public class BedwarsGameBucket {

    private final List<BedwarsGame> games = new CopyOnWriteArrayList<>();

    public void add(BedwarsGame game) {
        games.add(game);
    }

    public void remove(BedwarsGame game) {
        games.remove(game);
    }

    public BedwarsGame byFight(DuelFightModel fightModel) {
        return games.stream().filter(game -> game.getFightModel() == fightModel).findFirst().orElse(null);
    }

    public BedwarsGame byPlayer(UUID uuid) {
        return games.stream().filter(game -> game.teamOf(uuid) != null).findFirst().orElse(null);
    }

    public List<BedwarsGame> all() {
        return games;
    }
}

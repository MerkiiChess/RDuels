package ru.merkii.rduels.core.tnttag.bucket;

import jakarta.inject.Singleton;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.tnttag.game.TntTagGame;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class TntTagGameBucket {

    private final List<TntTagGame> games = new CopyOnWriteArrayList<>();

    public void add(TntTagGame game) {
        games.add(game);
    }

    public void remove(TntTagGame game) {
        games.remove(game);
    }

    public TntTagGame byFight(DuelFightModel fightModel) {
        return games.stream().filter(game -> game.getFightModel() == fightModel).findFirst().orElse(null);
    }

    public TntTagGame byPlayer(UUID uuid) {
        return games.stream().filter(game -> game.getAlive().contains(uuid)).findFirst().orElse(null);
    }

    public List<TntTagGame> all() {
        return games;
    }
}

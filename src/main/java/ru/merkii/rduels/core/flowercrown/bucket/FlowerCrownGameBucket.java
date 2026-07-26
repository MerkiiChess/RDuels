package ru.merkii.rduels.core.flowercrown.bucket;

import jakarta.inject.Singleton;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.flowercrown.game.FlowerCrownGame;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class FlowerCrownGameBucket {

    private final List<FlowerCrownGame> games = new CopyOnWriteArrayList<>();

    public void add(FlowerCrownGame game) {
        games.add(game);
    }

    public void remove(FlowerCrownGame game) {
        games.remove(game);
    }

    public FlowerCrownGame byFight(DuelFightModel fightModel) {
        return games.stream().filter(game -> game.getFightModel() == fightModel).findFirst().orElse(null);
    }

    public FlowerCrownGame byPlayer(UUID uuid) {
        return games.stream().filter(game -> involves(game.getFightModel(), uuid)).findFirst().orElse(null);
    }

    private boolean involves(DuelFightModel fightModel, UUID uuid) {
        if (fightModel.getSender() != null && fightModel.getSender().getUUID().equals(uuid)) return true;
        if (fightModel.getReceiver() != null && fightModel.getReceiver().getUUID().equals(uuid)) return true;
        if (fightModel.getPlayer2() != null && fightModel.getPlayer2().getUUID().equals(uuid)) return true;
        if (fightModel.getPlayer4() != null && fightModel.getPlayer4().getUUID().equals(uuid)) return true;
        if (fightModel.getSenderParty() != null && (fightModel.getSenderParty().getOwner().equals(uuid)
                || fightModel.getSenderParty().getPlayers().contains(uuid))) return true;
        return fightModel.getReceiverParty() != null && (fightModel.getReceiverParty().getOwner().equals(uuid)
                || fightModel.getReceiverParty().getPlayers().contains(uuid));
    }
}

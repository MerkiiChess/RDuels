package ru.merkii.rduels.core.duel.bucket;

import lombok.Getter;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DuelFightBucket {

    private final List<DuelFightModel> duelFights = new ArrayList<>();

    public void addFight(DuelFightModel duelFightModel) {
        this.duelFights.add(duelFightModel);
    }

    public void removeFight(DuelFightModel duelFightModel) {
        this.duelFights.remove(duelFightModel);
    }

}

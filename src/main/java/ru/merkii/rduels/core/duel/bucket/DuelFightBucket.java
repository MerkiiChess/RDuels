package ru.merkii.rduels.core.duel.bucket;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DuelFightBucket {

    List<DuelFightModel> duelFights = new ArrayList<>();

    public void addFight(DuelFightModel duelFightModel) {
        this.duelFights.add(duelFightModel);
    }

    public void removeFight(DuelFightModel duelFightModel) {
        this.duelFights.remove(duelFightModel);
    }

}

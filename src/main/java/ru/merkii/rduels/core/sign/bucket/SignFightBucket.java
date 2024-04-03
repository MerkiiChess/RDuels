package ru.merkii.rduels.core.sign.bucket;

import lombok.Getter;
import ru.merkii.rduels.core.sign.model.SignModel;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SignFightBucket {

    private final List<SignModel> fightSigns = new ArrayList<>();

    public void add(SignModel signModel) {
        this.fightSigns.add(signModel);
    }

    public void remove(SignModel signModel) {
        this.fightSigns.remove(signModel);
    }

}

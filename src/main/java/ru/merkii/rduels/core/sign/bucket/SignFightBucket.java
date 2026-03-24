package ru.merkii.rduels.core.sign.bucket;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.core.sign.model.SignModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SignFightBucket {

    List<SignModel> fightSigns = new ArrayList<>();

    public void add(SignModel signModel) {
        this.fightSigns.add(signModel);
    }

    public void remove(SignModel signModel) {
        this.fightSigns.remove(signModel);
    }

}

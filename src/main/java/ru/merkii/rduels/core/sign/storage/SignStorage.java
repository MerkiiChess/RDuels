package ru.merkii.rduels.core.sign.storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.config.settings.Config;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelType;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.model.BlockPosition;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignStorage extends Config {

    List<SignModel> signs = new ArrayList<>();

}

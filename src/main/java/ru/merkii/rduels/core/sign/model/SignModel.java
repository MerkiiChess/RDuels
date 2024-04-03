package ru.merkii.rduels.core.sign.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelType;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.KitModel;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public class SignModel {

    final BlockPosition blockPosition;
    final DuelType duelType;
    final DuelKitType duelKit;
    @Setter
    KitModel kitModel;

}

package ru.merkii.rduels.core.customkit.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomKitModel {

    String displayName;
    String permission;
    boolean invisible;

}

package ru.merkii.rduels.core.customkit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomKitModel {

    private String displayName;
    private int slot;
    private String permission;
    private boolean invisible;

    public static CustomKitModel create(String displayName, int slot, String permission, boolean invisible) {
        return new CustomKitModel(displayName, slot, permission, invisible);
    }

}

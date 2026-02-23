package ru.merkii.rduels.core.duel.model;

public enum DuelType {

    ONE,
    TWO;

    public int getSize() {
        return this == ONE ? 2 : 4;
    }

    public static DuelType fromString(String str) {
        return str.equalsIgnoreCase("1v1") ? ONE : (str.equalsIgnoreCase("2v2") ? TWO : null);
    }

}

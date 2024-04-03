package ru.merkii.rduels.core.duel.model;

import ru.merkii.rduels.RDuels;

public enum DuelKitType {

    CUSTOM,
    SERVER;

    public String getMessage() {
        return this == CUSTOM ? RDuels.getInstance().getPluginMessage().getMessage("customReplacer") : RDuels.getInstance().getPluginMessage().getMessage("serverReplacer");
    }

    public static DuelKitType fromString(String string) {
        return string.equalsIgnoreCase("custom") ? CUSTOM : (string.equalsIgnoreCase("server") ? SERVER : null);
    }

}

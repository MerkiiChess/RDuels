package ru.merkii.rduels.core.bedwars.game;

/** The two Bedwars teams, mapped onto the duel's sender/receiver sides. */
public enum TeamSide {
    SENDER,
    RECEIVER;

    public TeamSide opposite() {
        return this == SENDER ? RECEIVER : SENDER;
    }
}

package ru.merkii.rduels.lamp.exception;

public class DuelPlayerException extends RuntimeException {

    private final String playerName;

    public DuelPlayerException(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

}

package ru.merkii.rduels.gui.internal.click;

public interface ClickHandler {
    String PLAYER = "player";
    String ITEM_CONFIG = "itemConfig";

    static ClickHandler empty() {
        return () -> {
        };
    }

    default ClickHandler prepend(ClickHandler other) {
        return other.push(this);
    }

    default ClickHandler push(ClickHandler other) {
        return () -> {
            this.handle();
            other.handle();
        };
    }

    void handle();

}

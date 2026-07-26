package ru.merkii.rduels.core.tnttag.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

@ConfigInterface
public interface TntTagConfiguration {

    boolean enabled();

    /** Bomb fuse length in seconds. */
    default int fuseSeconds() {
        return 15;
    }

    /** Ticks after receiving the bomb during which the new holder cannot pass it back. */
    default int passCooldownTicks() {
        return 20;
    }

}

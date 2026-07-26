package ru.merkii.rduels.core.flowercrown.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface FlowerCrownConfiguration {

    boolean enabled();

    /** Flowers a side must steal from opponents to win. */
    default int flowersToWin() {
        return 5;
    }

    /** Block materials that count as flowers; empty = all small vanilla flowers. */
    default List<String> flowerMaterials() {
        return List.of();
    }

}

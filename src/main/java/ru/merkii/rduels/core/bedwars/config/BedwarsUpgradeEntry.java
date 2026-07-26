package ru.merkii.rduels.core.bedwars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

/** A team upgrade offered in the upgrade shop. */
@ConfigInterface
public interface BedwarsUpgradeEntry {

    /** Upgrade type: SHARPNESS, PROTECTION, HASTE, FORGE or HEAL_POOL. */
    String type();

    int slot();

    String material();

    default String name() {
        return "";
    }

    /** Ordered levels (level 1 first). */
    List<UpgradeLevel> levels();

}

package ru.merkii.rduels.core.bedwars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

/** One level of a team upgrade. */
@ConfigInterface
public interface UpgradeLevel {

    /** Currency: IRON, GOLD, DIAMOND or EMERALD. */
    String costResource();

    int costAmount();

    /** Effect strength for the level (enchant level / potion amplifier / forge step). */
    int value();

}

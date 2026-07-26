package ru.merkii.rduels.core.economy.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

@ConfigInterface
public interface EconomyConfiguration {

    boolean enabled();

    /** Cost to create a party; 0 disables the charge. */
    double partyCost();

    /** Money paid to the winner of a fight; 0 disables the reward. */
    double winReward();

    /** Only pay the win reward for ranked (queue) fights. */
    boolean winRewardRankedOnly();

}

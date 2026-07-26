package ru.merkii.rduels.core.randomkit.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface RandomKitConfiguration {

    boolean enabled();

    /** Only pick from these server-kit display names; empty = all kits. */
    List<String> include();

    /** Never pick these server-kit display names. */
    List<String> exclude();

}

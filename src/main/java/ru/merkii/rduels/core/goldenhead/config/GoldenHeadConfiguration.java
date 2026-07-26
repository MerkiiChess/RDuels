package ru.merkii.rduels.core.goldenhead.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface GoldenHeadConfiguration {

    boolean enabled();

    /** Material identifying the golden head (e.g. GOLDEN_APPLE or PLAYER_HEAD). */
    String material();

    /** Display name (MiniMessage) the item must have to count as a golden head; empty = match by material only. */
    String displayName();

    /** Extra effects applied on consume, each "TYPE,durationSeconds,amplifier" (e.g. "ABSORPTION,120,0"). */
    List<String> effects();

}

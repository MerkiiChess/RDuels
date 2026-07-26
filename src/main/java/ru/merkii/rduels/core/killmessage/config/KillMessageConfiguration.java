package ru.merkii.rduels.core.killmessage.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.Map;

@ConfigInterface
public interface KillMessageConfiguration {

    boolean enabled();

    /** true — announce to the whole server; false — only to the fight participants. */
    boolean broadcastGlobal();

    /** Fallback message when no cause-specific entry matches. Placeholders (victim), (killer). */
    String defaultMessage();

    /** Word substituted for (killer) on environmental deaths (void, fall, ...). */
    String environmentName();

    /**
     * Message per damage cause, keyed by the lower-case {@code EntityDamageEvent.DamageCause}
     * name (e.g. entity_attack, projectile, void, fall, fire_tick, lava, magic).
     * Placeholders: (victim), (killer).
     */
    Map<String, String> causes();

}

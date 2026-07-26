package ru.merkii.rduels.core.killeffect.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface KillEffectConfiguration {

    boolean enabled();

    /** Effects offered in the /killeffect menu, in display order. */
    List<KillEffectEntry> effects();

}

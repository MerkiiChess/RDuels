package ru.merkii.rduels.core.killeffect.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

/** One selectable kill effect as presented in the /killeffect menu. */
@ConfigInterface
public interface KillEffectEntry {

    /** Effect id, must match a {@link ru.merkii.rduels.core.killeffect.effect.KillEffect#id()}. */
    String id();

    /** Icon material for the menu. */
    String material();

    /** Display name (MiniMessage). */
    String name();

    /** Permission required to select it; empty/absent means everyone. */
    default String permission() {
        return "";
    }

}

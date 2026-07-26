package ru.merkii.rduels.core.killeffect;

import jakarta.inject.Singleton;
import ru.merkii.rduels.core.killeffect.effect.KillEffect;
import ru.merkii.rduels.core.killeffect.effect.KillEffects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of available kill effects, keyed by id. Seeded with the built-in
 * {@link KillEffects}; other plugins/modules may {@link #register} their own.
 */
@Singleton
public class KillEffectRegistry {

    private final Map<String, KillEffect> effects = new LinkedHashMap<>();

    public KillEffectRegistry() {
        for (KillEffects effect : KillEffects.values()) {
            register(effect);
        }
    }

    public void register(KillEffect effect) {
        effects.put(effect.id().toLowerCase(), effect);
    }

    public Optional<KillEffect> get(String id) {
        return id == null ? Optional.empty() : Optional.ofNullable(effects.get(id.toLowerCase()));
    }

    public boolean contains(String id) {
        return id != null && effects.containsKey(id.toLowerCase());
    }

}

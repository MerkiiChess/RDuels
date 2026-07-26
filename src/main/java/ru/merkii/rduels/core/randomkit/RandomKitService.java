package ru.merkii.rduels.core.randomkit;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.core.randomkit.config.RandomKitConfiguration;
import ru.merkii.rduels.model.KitModel;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Picks a random server kit, honouring the include/exclude lists of random-kits.yml.
 * Safe on an empty pool (returns null instead of throwing).
 */
@Singleton
public class RandomKitService {

    private final KitConfiguration kitConfiguration;
    private final RandomKitConfiguration config;

    @Inject
    public RandomKitService(KitConfiguration kitConfiguration, RandomKitConfiguration config) {
        this.kitConfiguration = kitConfiguration;
        this.config = config;
    }

    @Nullable
    public KitModel pickRandom() {
        List<String> include = lower(config.include());
        List<String> exclude = lower(config.exclude());

        List<KitModel> pool = kitConfiguration.kits().keySet().stream()
                .filter(kit -> include.isEmpty() || include.contains(kit.getDisplayName().toLowerCase(Locale.ROOT)))
                .filter(kit -> !exclude.contains(kit.getDisplayName().toLowerCase(Locale.ROOT)))
                .toList();

        if (pool.isEmpty()) {
            return null;
        }
        return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    }

    private List<String> lower(List<String> values) {
        return values == null ? List.of()
                : values.stream().map(value -> value.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
    }

}

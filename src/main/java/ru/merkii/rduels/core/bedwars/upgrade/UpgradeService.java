package ru.merkii.rduels.core.bedwars.upgrade;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.core.bedwars.config.BedwarsUpgradeConfiguration;
import ru.merkii.rduels.core.bedwars.config.BedwarsUpgradeEntry;
import ru.merkii.rduels.core.bedwars.config.UpgradeLevel;
import ru.merkii.rduels.core.bedwars.game.BedwarsTeam;

/** Resolves upgrade levels/values from the config and applies level purchases to a team. */
@Singleton
public class UpgradeService {

    private final BedwarsUpgradeConfiguration config;

    @Inject
    public UpgradeService(BedwarsUpgradeConfiguration config) {
        this.config = config;
    }

    @Nullable
    public BedwarsUpgradeEntry entry(UpgradeType type) {
        return config.upgrades().stream()
                .filter(entry -> type == UpgradeType.fromName(entry.type()))
                .findFirst().orElse(null);
    }

    public int maxLevel(UpgradeType type) {
        BedwarsUpgradeEntry entry = entry(type);
        return entry == null ? 0 : entry.levels().size();
    }

    public int currentLevel(BedwarsTeam team, UpgradeType type) {
        return team.getUpgradeLevel(type);
    }

    public boolean isActive(BedwarsTeam team, UpgradeType type) {
        return currentLevel(team, type) > 0;
    }

    /** Effect value at the team's current level, or -1 when the upgrade isn't purchased. */
    public int activeValue(BedwarsTeam team, UpgradeType type) {
        int level = currentLevel(team, type);
        BedwarsUpgradeEntry entry = entry(type);
        if (level < 1 || entry == null || level > entry.levels().size()) {
            return -1;
        }
        return entry.levels().get(level - 1).value();
    }

    /** The next purchasable level definition, or null when maxed / undefined. */
    @Nullable
    public UpgradeLevel nextLevel(BedwarsTeam team, UpgradeType type) {
        BedwarsUpgradeEntry entry = entry(type);
        int level = currentLevel(team, type);
        if (entry == null || level >= entry.levels().size()) {
            return null;
        }
        return entry.levels().get(level);
    }

    public void applyPurchase(BedwarsTeam team, UpgradeType type) {
        team.setUpgradeLevel(type, currentLevel(team, type) + 1);
    }
}

package ru.merkii.rduels.core.elo.api.provider;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.elo.api.EloAPI;
import ru.merkii.rduels.core.elo.config.EloConfiguration;
import ru.merkii.rduels.core.elo.config.TierConfiguration;
import ru.merkii.rduels.statistic.StatisticService;

import java.util.List;

@Singleton
public class EloAPIProvider implements EloAPI {

    private final StatisticService statisticService;
    private final EloConfiguration config;
    private final MessageConfig messages;

    @Inject
    public EloAPIProvider(StatisticService statisticService, EloConfiguration config, MessageConfig messages) {
        this.statisticService = statisticService;
        this.config = config;
        this.messages = messages;
    }

    @Override
    public int getElo(DuelPlayer player) {
        return statisticService.getElo(player.getUUID());
    }

    @Override
    public String getTierName(DuelPlayer player) {
        int elo = getElo(player);
        String name = "";
        for (TierConfiguration tier : config.tiers()) {
            if (elo >= tier.elo()) {
                name = tier.name();
            }
        }
        return name;
    }

    @Override
    public int calculateDelta(int winnerElo, int loserElo) {
        double expected = 1.0D / (1.0D + Math.pow(10.0D, (loserElo - winnerElo) / 400.0D));
        return Math.max(1, (int) Math.round(config.kFactor() * (1.0D - expected)));
    }

    @Override
    public void applyMatchResult(DuelPlayer winner, DuelPlayer loser) {
        int winnerElo = statisticService.getElo(winner.getUUID());
        int loserElo = statisticService.getElo(loser.getUUID());
        int delta = calculateDelta(winnerElo, loserElo);

        int newWinnerElo = statisticService.addElo(winner.getUUID(), delta);
        // Never let the loser drop below the configured floor.
        int loserDelta = Math.max(config.minElo(), loserElo - delta) - loserElo;
        int newLoserElo = statisticService.addElo(loser.getUUID(), loserDelta);

        messages.sendTo(winner, Placeholder.Placeholders.of(
                Placeholder.of("(elo)", String.valueOf(newWinnerElo)),
                Placeholder.of("(change)", String.valueOf(delta))
        ), "elo-win");
        messages.sendTo(loser, Placeholder.Placeholders.of(
                Placeholder.of("(elo)", String.valueOf(newLoserElo)),
                Placeholder.of("(change)", String.valueOf(-loserDelta))
        ), "elo-lose");

        grantTierRewards(winner, newWinnerElo);
        grantTierRewards(loser, newLoserElo);
    }

    /**
     * Grants every tier the player has reached but not yet been rewarded for. The stored
     * tier value is the count of already-granted tiers, so each reward runs exactly once.
     */
    private void grantTierRewards(DuelPlayer player, int elo) {
        List<TierConfiguration> tiers = config.tiers();
        if (tiers.isEmpty()) {
            return;
        }
        int reached = 0;
        for (TierConfiguration tier : tiers) {
            if (elo >= tier.elo()) {
                reached++;
            }
        }
        int granted = Math.max(0, statisticService.getTier(player.getUUID()));
        if (reached <= granted) {
            return;
        }
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        for (int i = granted; i < reached; i++) {
            TierConfiguration tier = tiers.get(i);
            messages.sendTo(player, Placeholder.wrapped("(tier)", tier.name()), "tier-up");
            if (bukkitPlayer != null) {
                tier.commands().forEach(command -> command.execute(bukkitPlayer));
            }
        }
        statisticService.setTierAtLeast(player.getUUID(), reached);
    }

}

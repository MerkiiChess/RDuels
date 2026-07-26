package ru.merkii.rduels.core.economy.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.economy.EconomyService;
import ru.merkii.rduels.core.economy.config.EconomyConfiguration;

/**
 * Pays the configured win reward to the winner of a fight (optionally only for ranked
 * queue fights). No-op when Vault/economy is unavailable.
 */
@Singleton
public class EconomyRewardListener implements Listener {

    private final EconomyService economyService;
    private final EconomyConfiguration config;
    private final MessageConfig messages;

    @Inject
    public EconomyRewardListener(EconomyService economyService, EconomyConfiguration config, MessageConfig messages) {
        this.economyService = economyService;
        this.config = config;
        this.messages = messages;
    }

    @EventHandler
    public void onStopFight(DuelStopFightEvent event) {
        Player winner = event.getWinner();
        if (!config.enabled() || winner == null || config.winReward() <= 0 || !economyService.isEnabled()) {
            return;
        }
        DuelFightModel fightModel = event.getDuelFightModel();
        if (config.winRewardRankedOnly() && !fightModel.isRanked()) {
            return;
        }
        if (economyService.deposit(winner, config.winReward())) {
            messages.sendTo(winner, Placeholder.wrapped("(amount)", economyService.format(config.winReward())), "economy-win-reward");
        }
    }

}

package ru.merkii.rduels.core.killeffect.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.event.DuelKillPlayerEvent;
import ru.merkii.rduels.core.killeffect.KillEffectRegistry;
import ru.merkii.rduels.core.killeffect.config.KillEffectConfiguration;
import ru.merkii.rduels.statistic.StatisticService;

/**
 * Plays the killer's selected kill effect at the victim's death location.
 * Runs at LOWEST priority so the victim is still at the death spot (the duel handler
 * teleports them to the killer at NORMAL priority).
 */
@Singleton
public class KillEffectListener implements Listener {

    private final KillEffectRegistry registry;
    private final KillEffectConfiguration config;
    private final StatisticService statisticService;

    @Inject
    public KillEffectListener(KillEffectRegistry registry, KillEffectConfiguration config, StatisticService statisticService) {
        this.registry = registry;
        this.config = config;
        this.statisticService = statisticService;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKill(DuelKillPlayerEvent event) {
        if (!config.enabled() || event.getKiller() == null || event.getVictim() == null) {
            return;
        }
        Player killer = BukkitAdapter.adapt(event.getKiller());
        Player victim = BukkitAdapter.adapt(event.getVictim());
        if (killer == null || victim == null) {
            return;
        }
        String effectId = statisticService.getKillEffect(killer.getUniqueId());
        if (effectId == null || effectId.isEmpty()) {
            return;
        }
        Location location = victim.getLocation();
        registry.get(effectId).ifPresent(effect -> effect.play(killer, victim, location));
    }

}

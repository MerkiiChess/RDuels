package ru.merkii.rduels.core.sumo.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.RoundOutcomeService;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.sumo.config.SumoConfiguration;
import ru.merkii.rduels.model.EntityPosition;

/**
 * Sumo mode: in a sumo-flagged arena a fighter loses the round by touching water or
 * falling below the arena spawn. 1v1 only (sumo rings are 1v1 by design).
 */
@Singleton
public class SumoListener implements Listener {

    private final SumoConfiguration config;
    private final DuelAPI duelAPI;
    private final RoundOutcomeService roundOutcomeService;

    @Inject
    public SumoListener(SumoConfiguration config, DuelAPI duelAPI, RoundOutcomeService roundOutcomeService) {
        this.config = config;
        this.duelAPI = duelAPI;
        this.roundOutcomeService = roundOutcomeService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!config.enabled() || !movedBlock(event)) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getGameMode().name().equals("SPECTATOR")) {
            return;
        }
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        DuelFightModel fightModel = duelAPI.getFightModelFromPlayer(duelPlayer);
        if (fightModel == null || duelAPI.isNoMovePlayer(duelPlayer)) {
            return;
        }
        ArenaModel arena = fightModel.getArenaModel();
        if (!arena.isSumo()) {
            return;
        }
        Location to = event.getTo();
        if (isLoss(to, arena)) {
            roundOutcomeService.loseRoundOneVsOne(duelPlayer);
        }
    }

    private boolean isLoss(Location to, ArenaModel arena) {
        if (config.detectWater() && isWater(to)) {
            return true;
        }
        EntityPosition reference = arena.getOnePosition();
        return reference != null && to.getY() < reference.getY() - config.fallDistance();
    }

    private boolean isWater(Location location) {
        Material atFeet = location.getBlock().getType();
        Material below = location.clone().subtract(0, 1, 0).getBlock().getType();
        return atFeet == Material.WATER || below == Material.WATER;
    }

    private boolean movedBlock(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        return to != null && (from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ());
    }

}

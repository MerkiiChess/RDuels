package ru.merkii.rduels.core.boundary.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.boundary.config.BoundaryConfiguration;
import ru.merkii.rduels.core.duel.RoundOutcomeService;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.model.EntityPosition;

/**
 * Arena boundary + void protection for active fighters: warns when they approach the
 * edge, and either teleports them back or makes them lose the round when they fall into
 * the void or leave the arena radius.
 */
@Singleton
public class BoundaryListener implements Listener {

    private final BoundaryConfiguration config;
    private final DuelAPI duelAPI;
    private final RoundOutcomeService roundOutcomeService;

    @Inject
    public BoundaryListener(BoundaryConfiguration config, DuelAPI duelAPI, RoundOutcomeService roundOutcomeService) {
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
        Location to = event.getTo();
        ArenaModel arena = fightModel.getArenaModel();
        // Skywars/Bedwars handle their own falls (void = death, no teleport-back).
        if (arena.isSkywars() || arena.isBedwars()) {
            return;
        }

        if (to.getY() < config.voidMinY()) {
            handleOut(player, duelPlayer, arena);
            return;
        }
        if (config.maxRadius() > 0) {
            Location center = center(arena, to.getWorld());
            if (center != null) {
                double distance = horizontalDistance(to, center);
                if (distance > config.maxRadius()) {
                    handleOut(player, duelPlayer, arena);
                } else if (config.warnDistance() > 0 && distance > config.maxRadius() - config.warnDistance()) {
                    int blocksLeft = (int) Math.ceil(config.maxRadius() - distance);
                    player.sendActionBar(MiniMessage.miniMessage()
                            .deserialize(config.warnMessage().replace("(blocks)", String.valueOf(blocksLeft))));
                }
            }
        }
    }

    private void handleOut(Player player, DuelPlayer duelPlayer, ArenaModel arena) {
        if ("KILL".equalsIgnoreCase(config.voidAction()) && roundOutcomeService.loseRoundOneVsOne(duelPlayer)) {
            return;
        }
        // TELEPORT_BACK (also the fallback when a KILL could not be applied, e.g. in 2v2).
        EntityPosition spawn = sideSpawn(arena, duelPlayer, player);
        if (spawn != null) {
            duelPlayer.teleport(spawn);
        }
    }

    private EntityPosition sideSpawn(ArenaModel arena, DuelPlayer duelPlayer, Player player) {
        if (arena.isFfa()) {
            return arena.getFfaPositions().getOrDefault(1, arena.getSpectatorPosition());
        }
        DuelFightModel fightModel = duelAPI.getFightModelFromPlayer(duelPlayer);
        boolean isSender = fightModel != null && fightModel.getSender() != null
                && fightModel.getSender().getUUID().equals(player.getUniqueId());
        return isSender ? arena.getOnePosition() : arena.getTwoPosition();
    }

    private Location center(ArenaModel arena, org.bukkit.World world) {
        EntityPosition reference = arena.isFfa()
                ? arena.getFfaPositions().get(1)
                : arena.getOnePosition();
        if (reference == null) {
            return null;
        }
        Location location = reference.toLocation();
        return location.getWorld() != null ? location : null;
    }

    private double horizontalDistance(Location a, Location b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private boolean movedBlock(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        return to != null && (from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ());
    }

}

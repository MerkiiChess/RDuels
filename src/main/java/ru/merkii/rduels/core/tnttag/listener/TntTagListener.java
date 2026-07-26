package ru.merkii.rduels.core.tnttag.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.event.DuelStartFightEvent;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.tnttag.bucket.TntTagGameBucket;
import ru.merkii.rduels.core.tnttag.config.TntTagConfiguration;
import ru.merkii.rduels.core.tnttag.game.TntTagGame;
import ru.merkii.rduels.model.EntityPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * TNT Tag: one player holds a live bomb that passes on a hit (with a short cooldown).
 * When the fuse ends the holder is eliminated; the last player alive wins. Players take
 * no other damage — only the bomb removes anyone.
 */
@Singleton
public class TntTagListener implements Listener {

    private final RDuels plugin;
    private final DuelAPI duelAPI;
    private final TntTagConfiguration config;
    private final TntTagGameBucket gameBucket;
    private final MessageConfig messages;

    @Inject
    public TntTagListener(RDuels plugin, DuelAPI duelAPI, TntTagConfiguration config,
                          TntTagGameBucket gameBucket, MessageConfig messages) {
        this.plugin = plugin;
        this.duelAPI = duelAPI;
        this.config = config;
        this.gameBucket = gameBucket;
        this.messages = messages;
    }

    @EventHandler
    public void onStart(DuelStartFightEvent event) {
        DuelFightModel fightModel = event.getDuelFightModel();
        if (!config.enabled() || !fightModel.getArenaModel().isTntTag()) {
            return;
        }
        TntTagGame game = new TntTagGame(fightModel);
        game.getAlive().addAll(participants(fightModel));
        if (game.getAlive().size() < 2) {
            return;
        }
        game.setFuseLeft(config.fuseSeconds());
        assignBomb(game, randomAlive(game, null));
        gameBucket.add(game);

        int taskId = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> tick(game), 20L, 20L).getTaskId();
        game.setTaskId(taskId);
    }

    private void tick(TntTagGame game) {
        Player holder = holder(game);
        if (holder == null) {
            reassignAfterLoss(game, game.getHolder());
            return;
        }
        int left = game.decrementFuse();
        if (left <= 0) {
            explode(game, holder);
            return;
        }
        holder.sendActionBar(Component.text("💣 " + left));
        holder.playSound(holder.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 2.0F);
    }

    private void explode(TntTagGame game, Player holder) {
        Location location = holder.getLocation();
        if (location.getWorld() != null) {
            location.getWorld().spawnParticle(particle("EXPLOSION_EMITTER", "EXPLOSION_HUGE"), location, 1);
            location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
        }
        holder.setGlowing(false);
        UUID eliminated = holder.getUniqueId();
        reassignAfterLoss(game, eliminated);
    }

    /** Removes the given player from the game (bomb explosion) and continues or ends it. */
    private void reassignAfterLoss(TntTagGame game, UUID eliminatedUuid) {
        game.getAlive().remove(eliminatedUuid);
        DuelPlayer eliminated = BukkitAdapter.getPlayer(eliminatedUuid);
        if (eliminated != null) {
            eliminated.setGameMode(GameMode.SPECTATOR);
            EntityPosition spectator = game.getFightModel().getArenaModel().getSpectatorPosition();
            if (spectator != null) {
                eliminated.teleport(spectator);
            }
            messages.sendTo(eliminated, "tnttag-eliminated");
        }

        if (game.getAlive().size() <= 1) {
            finish(game, eliminatedUuid);
            return;
        }
        game.setFuseLeft(config.fuseSeconds());
        assignBomb(game, randomAlive(game, null));
    }

    private void finish(TntTagGame game, UUID lastLoser) {
        stopTask(game);
        gameBucket.remove(game);
        UUID winnerUuid = game.getAlive().stream().findFirst().orElse(null);
        DuelPlayer winner = winnerUuid == null ? null : BukkitAdapter.getPlayer(winnerUuid);
        DuelPlayer loser = BukkitAdapter.getPlayer(lastLoser);
        plugin.getServer().getScheduler().runTask(plugin, () -> duelAPI.stopFight(game.getFightModel(), winner, loser));
    }

    private void assignBomb(TntTagGame game, UUID newHolder) {
        if (newHolder == null) {
            return;
        }
        game.setHolder(newHolder);
        game.setPassLockUntil(System.currentTimeMillis() + config.passCooldownTicks() * 50L);
        // Glow only the current holder.
        for (UUID uuid : game.getAlive()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                player.setGlowing(uuid.equals(newHolder));
            }
        }
        Player holder = plugin.getServer().getPlayer(newHolder);
        if (holder != null) {
            holder.playSound(holder.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0F, 1.0F);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        Player damager = resolveDamager(event);
        if (damager == null) {
            return;
        }
        TntTagGame game = gameBucket.byPlayer(victim.getUniqueId());
        if (game == null || !game.isAlive(damager.getUniqueId()) || !game.isAlive(victim.getUniqueId())) {
            return;
        }
        // No PvP damage in TNT Tag — only the bomb removes a player.
        event.setCancelled(true);
        // Passing: only the holder passes, and only after the pass cooldown.
        if (game.isHolder(damager.getUniqueId()) && System.currentTimeMillis() >= game.getPassLockUntil()) {
            assignBomb(game, victim.getUniqueId());
            Component message = messages.message(Placeholder.wrapped("(player)", victim.getName()), "tnttag-passed");
            onlinePlayers(game).forEach(p -> p.sendMessage(message));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent || !(event.getEntity() instanceof Player player)) {
            return;
        }
        // Keep everyone alive from environment/fall damage; only the bomb eliminates.
        if (gameBucket.byPlayer(player.getUniqueId()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onStop(DuelStopFightEvent event) {
        TntTagGame game = gameBucket.byFight(event.getDuelFightModel());
        if (game != null) {
            stopTask(game);
            unglow(game);
            gameBucket.remove(game);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        TntTagGame game = gameBucket.byPlayer(event.getPlayer().getUniqueId());
        if (game != null && game.isAlive(event.getPlayer().getUniqueId())) {
            reassignAfterLoss(game, event.getPlayer().getUniqueId());
        }
    }

    private void stopTask(TntTagGame game) {
        if (game.getTaskId() != -1) {
            plugin.getServer().getScheduler().cancelTask(game.getTaskId());
            game.setTaskId(-1);
        }
    }

    private void unglow(TntTagGame game) {
        for (UUID uuid : game.getAlive()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                player.setGlowing(false);
            }
        }
    }

    private Player resolveDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            return player;
        }
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            return shooter;
        }
        return null;
    }

    private Player holder(TntTagGame game) {
        return game.getHolder() == null ? null : plugin.getServer().getPlayer(game.getHolder());
    }

    private UUID randomAlive(TntTagGame game, UUID exclude) {
        List<UUID> candidates = new ArrayList<>(game.getAlive());
        candidates.remove(exclude);
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    private List<Player> onlinePlayers(TntTagGame game) {
        List<Player> players = new ArrayList<>();
        for (UUID uuid : game.getAlive()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    private List<UUID> participants(DuelFightModel fightModel) {
        List<UUID> players = new ArrayList<>();
        addPlayer(players, fightModel.getSender());
        addPlayer(players, fightModel.getReceiver());
        addPlayer(players, fightModel.getPlayer2());
        addPlayer(players, fightModel.getPlayer4());
        if (fightModel.getSenderParty() != null) {
            players.addAll(fightModel.getSenderParty().getPlayers());
            players.add(fightModel.getSenderParty().getOwner());
        }
        if (fightModel.getReceiverParty() != null) {
            players.addAll(fightModel.getReceiverParty().getPlayers());
            players.add(fightModel.getReceiverParty().getOwner());
        }
        return players;
    }

    private void addPlayer(List<UUID> players, DuelPlayer duelPlayer) {
        if (duelPlayer != null && !players.contains(duelPlayer.getUUID())) {
            players.add(duelPlayer.getUUID());
        }
    }

    private Particle particle(String primary, String fallback) {
        try {
            return Particle.valueOf(primary);
        } catch (IllegalArgumentException exception) {
            try {
                return Particle.valueOf(fallback);
            } catch (IllegalArgumentException ignored) {
                return Particle.CLOUD;
            }
        }
    }
}

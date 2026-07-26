package ru.merkii.rduels.core.duel.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.arena.bucket.ArenaBlockBuildBucket;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.KitRules;

/**
 * Enforces the per-kit gameplay rules (kits.yml {@code rules} section) during fights:
 * UHC hunger freeze, crafting bans, damage multipliers, build height limits,
 * item cooldowns and temporary block decay.
 */
@Singleton
public class KitRulesListener implements Listener {

    private final RDuels plugin;
    private final DuelAPI duelAPI;
    private final ArenaBlockBuildBucket arenaBlockBuildBucket;
    private final MessageConfig messages;

    @Inject
    public KitRulesListener(RDuels plugin, DuelAPI duelAPI, ArenaBlockBuildBucket arenaBlockBuildBucket, MessageConfig messages) {
        this.plugin = plugin;
        this.duelAPI = duelAPI;
        this.arenaBlockBuildBucket = arenaBlockBuildBucket;
        this.messages = messages;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        KitRules rules = rulesOf(player);
        if (rules != null && rules.isNoHunger()) {
            event.setCancelled(true);
        }
    }

    // Note: crafting is blocked globally during fights by DuelListener#onCraft, so the
    // per-kit disable-crafting rule needs no handler here (the field stays reserved).

    // HIGH: runs after the same-team damage cancellation in DuelListener.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Player damager = resolveDamager(event);
        if (damager == null) {
            return;
        }
        KitRules rules = rulesOf(damager);
        if (rules != null && rules.hasDamageMultiplier()) {
            event.setDamage(event.getDamage() * rules.getDamageMultiplier());
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

    // HIGH: runs after the arena build checks in BlockListener.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        DuelFightModel fightModel = duelAPI.getFightModelFromPlayer(duelPlayer);
        if (fightModel == null || fightModel.getKitModel() == null) {
            return;
        }
        KitRules rules = fightModel.getKitModel().getRules();
        if (rules == null) {
            return;
        }

        if (rules.hasBuildHeight()) {
            Integer referenceY = arenaReferenceY(fightModel.getArenaModel());
            if (referenceY != null && event.getBlock().getY() > referenceY + rules.getBuildHeight()) {
                event.setCancelled(true);
                messages.sendTo(player, "kit-build-height");
                return;
            }
        }

        if (rules.hasBlockDecay()) {
            scheduleDecay(fightModel.getArenaModel(), event.getBlock(), rules.getBlockDecaySeconds());
        }
    }

    private void scheduleDecay(ArenaModel arenaModel, Block block, int seconds) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // Only decay blocks still tracked as player-built: after an arena restore the
            // bucket is cleared, so we never punch holes into a freshly pasted schematic.
            if (arenaBlockBuildBucket.isExistsBlock(arenaModel, block)) {
                arenaBlockBuildBucket.removeBlock(arenaModel, block);
                block.setType(Material.AIR);
            }
        }, seconds * 20L);
    }

    private Integer arenaReferenceY(ArenaModel arenaModel) {
        if (arenaModel == null) {
            return null;
        }
        EntityPosition position = arenaModel.isFfa()
                ? arenaModel.getFfaPositions().get(1)
                : arenaModel.getOnePosition();
        return position == null ? null : (int) position.getY();
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        applyCooldown(event.getPlayer(), event.getItem().getType());
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            applyCooldown(player, launchMaterial(event.getEntity()));
        }
    }

    private Material launchMaterial(Projectile projectile) {
        return switch (projectile.getType()) {
            case ENDER_PEARL -> Material.ENDER_PEARL;
            case SNOWBALL -> Material.SNOWBALL;
            case EGG -> Material.EGG;
            default -> null;
        };
    }

    /** Starts the native item cooldown, which blocks reuse both client- and server-side. */
    private void applyCooldown(Player player, Material material) {
        if (material == null) {
            return;
        }
        KitRules rules = rulesOf(player);
        if (rules == null) {
            return;
        }
        Integer seconds = rules.getItemCooldowns().get(material);
        if (seconds != null && seconds > 0) {
            player.setCooldown(material, seconds * 20);
        }
    }

    private KitRules rulesOf(Player player) {
        DuelFightModel fightModel = duelAPI.getFightModelFromPlayer(BukkitAdapter.adapt(player));
        if (fightModel == null || fightModel.getKitModel() == null) {
            return null;
        }
        return fightModel.getKitModel().getRules();
    }

}

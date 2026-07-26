package ru.merkii.rduels.core.bedwars.generator;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.bedwars.config.BedwarsConfiguration;
import ru.merkii.rduels.core.bedwars.config.GeneratorMarker;
import ru.merkii.rduels.core.bedwars.game.BedwarsGame;
import ru.merkii.rduels.core.bedwars.game.TeamSide;
import ru.merkii.rduels.core.bedwars.resource.ResourceType;
import ru.merkii.rduels.core.bedwars.upgrade.UpgradeType;
import ru.merkii.rduels.model.EntityPosition;

import java.util.List;
import java.util.Map;

/** Scans an arena for generator markers and drives the per-game generator loop. */
@Singleton
public class GeneratorService {

    private static final int TICK_PERIOD = 1;

    private final RDuels plugin;
    private final BedwarsConfiguration config;
    private final ru.merkii.rduels.core.bedwars.upgrade.UpgradeService upgradeService;

    @Inject
    public GeneratorService(RDuels plugin, BedwarsConfiguration config,
                            ru.merkii.rduels.core.bedwars.upgrade.UpgradeService upgradeService) {
        this.plugin = plugin;
        this.config = config;
        this.upgradeService = upgradeService;
    }

    /** Finds marker blocks in the arena and populates the game's generator list. */
    public void buildGenerators(BedwarsGame game, ArenaModel arena) {
        Location center = center(arena);
        if (center == null || center.getWorld() == null) {
            return;
        }
        World world = center.getWorld();
        int radius = Math.max(1, config.scanRadius());
        Map<String, GeneratorMarker> markers = config.generators();
        int cx = center.getBlockX(), cy = center.getBlockY(), cz = center.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int y = cy - radius; y <= cy + radius; y++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    GeneratorMarker marker = markers.get(block.getType().name());
                    if (marker == null) {
                        continue;
                    }
                    ResourceType resource = ResourceType.fromName(marker.resource());
                    if (resource == null) {
                        continue;
                    }
                    Location spawn = block.getLocation().add(0.5, 1.0, 0.5);
                    TeamSide team = marker.team() ? nearestSide(arena, block.getLocation()) : null;
                    game.getGenerators().add(new Generator(spawn, resource, marker.interval(), team));
                }
            }
        }
    }

    public void startGenerators(BedwarsGame game) {
        int taskId = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> tick(game), TICK_PERIOD, TICK_PERIOD).getTaskId();
        game.setGeneratorTaskId(taskId);
    }

    public void stopGenerators(BedwarsGame game) {
        if (game.getGeneratorTaskId() != -1) {
            plugin.getServer().getScheduler().cancelTask(game.getGeneratorTaskId());
            game.setGeneratorTaskId(-1);
        }
    }

    private void tick(BedwarsGame game) {
        // Heal Pool: regenerate team members near their bed once a second.
        if (game.tickHealPoolCounter() % 20 == 0) {
            applyHealPool(game, game.getSenderTeam());
            applyHealPool(game, game.getReceiverTeam());
        }
        for (Generator generator : game.getGenerators()) {
            double forgeStep = 1.0D;
            if (generator.getTeam() != null) {
                int forgeValue = upgradeService.activeValue(game.team(generator.getTeam()), UpgradeType.FORGE);
                if (forgeValue > 0) {
                    forgeStep = 1.0D + forgeValue;
                }
            }
            if (generator.tick(forgeStep)) {
                spawn(generator);
            }
        }
    }

    private static final double HEAL_POOL_RADIUS = 12.0D;

    private void applyHealPool(BedwarsGame game, ru.merkii.rduels.core.bedwars.game.BedwarsTeam team) {
        if (!upgradeService.isActive(team, ru.merkii.rduels.core.bedwars.upgrade.UpgradeType.HEAL_POOL) || team.getSpawn() == null) {
            return;
        }
        Location spawn = team.getSpawn().toLocation();
        org.bukkit.potion.PotionEffectType regen = org.bukkit.potion.PotionEffectType.getByName("REGENERATION");
        if (spawn.getWorld() == null || regen == null) {
            return;
        }
        for (java.util.UUID uuid : team.getMembers()) {
            org.bukkit.entity.Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.getWorld().equals(spawn.getWorld())
                    && player.getLocation().distanceSquared(spawn) <= HEAL_POOL_RADIUS * HEAL_POOL_RADIUS) {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(regen, 40, 0, true, false));
            }
        }
    }

    private void spawn(Generator generator) {
        Location location = generator.getLocation();
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        Material material = generator.getResource().material();
        GeneratorMarker marker = config.generators().values().stream()
                .filter(m -> generator.getResource().name().equalsIgnoreCase(m.resource()))
                .findFirst().orElse(null);
        int cap = marker == null ? 48 : marker.maxNearby();
        long nearby = world.getNearbyEntities(location, 2.0, 2.0, 2.0).stream()
                .filter(entity -> entity instanceof Item item && item.getItemStack().getType() == material)
                .count();
        if (nearby >= cap) {
            return;
        }
        Item dropped = world.dropItem(location, new ItemStack(material, 1));
        dropped.setVelocity(dropped.getVelocity().zero());
    }

    private TeamSide nearestSide(ArenaModel arena, Location location) {
        EntityPosition one = arena.getOnePosition();
        EntityPosition two = arena.getTwoPosition();
        if (one == null || two == null) {
            return TeamSide.SENDER;
        }
        return squared(location, one) <= squared(location, two) ? TeamSide.SENDER : TeamSide.RECEIVER;
    }

    private double squared(Location location, EntityPosition position) {
        double dx = location.getX() - position.getX();
        double dz = location.getZ() - position.getZ();
        return dx * dx + dz * dz;
    }

    private Location center(ArenaModel arena) {
        EntityPosition one = arena.getOnePosition();
        EntityPosition two = arena.getTwoPosition();
        if (one == null) {
            return two == null ? null : two.toLocation();
        }
        if (two == null) {
            return one.toLocation();
        }
        Location a = one.toLocation();
        Location b = two.toLocation();
        if (a.getWorld() == null) {
            return b;
        }
        return new Location(a.getWorld(), (a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2, (a.getZ() + b.getZ()) / 2);
    }
}

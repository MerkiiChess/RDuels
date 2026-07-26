package ru.merkii.rduels.core.skywars.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.event.DuelStartFightEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.skywars.config.SkywarsConfiguration;
import ru.merkii.rduels.core.skywars.config.SkywarsLootEntry;
import ru.merkii.rduels.core.skywars.config.SkywarsStarterItem;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Skywars mode with depth: tiered loot (better central chests), optional starter items,
 * and timed chest refills with an announcement. Single life is enforced by numGames=1.
 */
@Singleton
public class SkywarsListener implements Listener {

    private final RDuels plugin;
    private final SkywarsConfiguration config;
    private final DuelAPI duelAPI;

    @Inject
    public SkywarsListener(RDuels plugin, SkywarsConfiguration config, DuelAPI duelAPI) {
        this.plugin = plugin;
        this.config = config;
        this.duelAPI = duelAPI;
    }

    @EventHandler
    public void onStart(DuelStartFightEvent event) {
        DuelFightModel fightModel = event.getDuelFightModel();
        ArenaModel arena = fightModel.getArenaModel();
        if (!config.enabled() || !arena.isSkywars()) {
            return;
        }
        Location center = center(arena);
        List<Container> chests = findChests(arena, center);
        if (!chests.isEmpty()) {
            fill(chests, center);
        }
        giveStarter(fightModel);

        if (config.refillSeconds() > 0) {
            scheduleRefill(fightModel, arena);
        }
    }

    private void giveStarter(DuelFightModel fightModel) {
        List<SkywarsStarterItem> starter = config.starterItems();
        if (starter.isEmpty()) {
            return;
        }
        for (Player player : participants(fightModel)) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            for (SkywarsStarterItem item : starter) {
                Material material = Material.matchMaterial(item.material());
                if (material == null) {
                    continue;
                }
                ItemStack stack = new ItemStack(material, Math.max(1, item.amount()));
                item.enchants().forEach((name, level) -> {
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
                    if (enchantment != null) {
                        stack.addUnsafeEnchantment(enchantment, level);
                    }
                });
                equip(player, stack);
            }
            player.updateInventory();
        }
    }

    private void equip(Player player, ItemStack stack) {
        String name = stack.getType().name();
        if (name.endsWith("_HELMET")) player.getInventory().setHelmet(stack);
        else if (name.endsWith("_CHESTPLATE")) player.getInventory().setChestplate(stack);
        else if (name.endsWith("_LEGGINGS")) player.getInventory().setLeggings(stack);
        else if (name.endsWith("_BOOTS")) player.getInventory().setBoots(stack);
        else player.getInventory().addItem(stack);
    }

    private void scheduleRefill(DuelFightModel fightModel, ArenaModel arena) {
        DuelPlayer reference = fightModel.getSender();
        long period = config.refillSeconds() * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (reference == null || !duelAPI.isFightPlayer(reference)) {
                    cancel();
                    return;
                }
                Location center = center(arena);
                fill(findChests(arena, center), center);
                if (config.refillAnnounce()) {
                    Component message = MiniMessage.miniMessage().deserialize(config.refillMessage());
                    participants(fightModel).forEach(player -> player.sendMessage(message));
                }
            }
        }.runTaskTimer(plugin, period, period);
    }

    private List<Container> findChests(ArenaModel arena, Location center) {
        List<Container> chests = new ArrayList<>();
        if (center == null || center.getWorld() == null) {
            return chests;
        }
        World world = center.getWorld();
        int radius = Math.max(1, config.searchRadius());
        int cx = center.getBlockX(), cy = center.getBlockY(), cz = center.getBlockZ();
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int y = cy - radius; y <= cy + radius; y++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if ((block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST)
                            && block.getState() instanceof Container container) {
                        chests.add(container);
                    }
                }
            }
        }
        return chests;
    }

    private void fill(List<Container> chests, Location center) {
        List<SkywarsLootEntry> islandLoot = config.loot();
        List<SkywarsLootEntry> midLoot = config.midLoot().isEmpty() ? islandLoot : config.midLoot();
        int midRadiusSq = config.midRadius() * config.midRadius();

        for (Container container : chests) {
            boolean mid = center != null && midRadiusSq > 0
                    && container.getLocation().getWorld() != null
                    && container.getLocation().getWorld().equals(center.getWorld())
                    && container.getLocation().distanceSquared(center) <= midRadiusSq;
            List<SkywarsLootEntry> table = mid ? midLoot : islandLoot;
            if (table.isEmpty()) {
                continue;
            }
            fillChest(container.getInventory(), table);
        }
    }

    private void fillChest(Inventory inventory, List<SkywarsLootEntry> loot) {
        int totalWeight = loot.stream().mapToInt(entry -> Math.max(1, entry.weight())).sum();
        inventory.clear();
        int count = randomBetween(config.minItemsPerChest(), config.maxItemsPerChest());
        for (int i = 0; i < count; i++) {
            ItemStack stack = roll(loot, totalWeight);
            if (stack != null) {
                inventory.setItem(ThreadLocalRandom.current().nextInt(inventory.getSize()), stack);
            }
        }
    }

    private ItemStack roll(List<SkywarsLootEntry> loot, int totalWeight) {
        int pick = ThreadLocalRandom.current().nextInt(totalWeight);
        int cumulative = 0;
        SkywarsLootEntry chosen = null;
        for (SkywarsLootEntry entry : loot) {
            cumulative += Math.max(1, entry.weight());
            if (pick < cumulative) {
                chosen = entry;
                break;
            }
        }
        if (chosen == null) {
            return null;
        }
        Material material = Material.matchMaterial(chosen.material());
        if (material == null || material.isAir()) {
            return null;
        }
        return new ItemStack(material, randomBetween(chosen.minAmount(), chosen.maxAmount()));
    }

    private int randomBetween(int min, int max) {
        int lo = Math.max(1, Math.min(min, max));
        int hi = Math.max(lo, max);
        return lo == hi ? lo : ThreadLocalRandom.current().nextInt(lo, hi + 1);
    }

    private List<Player> participants(DuelFightModel fightModel) {
        List<Player> players = new ArrayList<>();
        addPlayer(players, fightModel.getSender());
        addPlayer(players, fightModel.getReceiver());
        addPlayer(players, fightModel.getPlayer2());
        addPlayer(players, fightModel.getPlayer4());
        if (fightModel.getSenderParty() != null) {
            players.addAll(PlayerUtil.convertListUUID(fightModel.getSenderParty().getPlayers()));
            addUuid(players, fightModel.getSenderParty().getOwner());
        }
        if (fightModel.getReceiverParty() != null) {
            players.addAll(PlayerUtil.convertListUUID(fightModel.getReceiverParty().getPlayers()));
            addUuid(players, fightModel.getReceiverParty().getOwner());
        }
        return players;
    }

    private void addPlayer(List<Player> players, DuelPlayer duelPlayer) {
        if (duelPlayer != null) {
            addUuid(players, duelPlayer.getUUID());
        }
    }

    private void addUuid(List<Player> players, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }

    private Location center(ArenaModel arena) {
        if (arena.isFfa()) {
            EntityPosition first = arena.getFfaPositions().get(1);
            return first == null ? null : first.toLocation();
        }
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

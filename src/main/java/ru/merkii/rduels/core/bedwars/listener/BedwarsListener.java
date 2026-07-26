package ru.merkii.rduels.core.bedwars.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.bedwars.bucket.BedwarsGameBucket;
import ru.merkii.rduels.core.bedwars.config.BedwarsConfiguration;
import ru.merkii.rduels.core.bedwars.game.BedwarsGame;
import ru.merkii.rduels.core.bedwars.game.BedwarsTeam;
import ru.merkii.rduels.core.bedwars.game.TeamSide;
import ru.merkii.rduels.core.bedwars.generator.GeneratorService;
import ru.merkii.rduels.core.bedwars.loadout.LoadoutService;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.event.DuelKillPlayerEvent;
import ru.merkii.rduels.core.duel.event.DuelStartFightEvent;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.model.EntityPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Full Bedwars engine (2 teams mapped to the duel's sender/receiver sides):
 * loadouts, resource generators, bed break, respawn while the bed stands, elimination
 * and team win. Shop/upgrades are driven by {@code BedwarsShopService}.
 */
@Singleton
public class BedwarsListener implements Listener {

    private final RDuels plugin;
    private final DuelAPI duelAPI;
    private final MessageConfig messages;
    private final BedwarsConfiguration config;
    private final BedwarsGameBucket gameBucket;
    private final GeneratorService generatorService;
    private final LoadoutService loadoutService;

    @Inject
    public BedwarsListener(RDuels plugin, DuelAPI duelAPI, MessageConfig messages, BedwarsConfiguration config,
                           BedwarsGameBucket gameBucket, GeneratorService generatorService, LoadoutService loadoutService) {
        this.plugin = plugin;
        this.duelAPI = duelAPI;
        this.messages = messages;
        this.config = config;
        this.gameBucket = gameBucket;
        this.generatorService = generatorService;
        this.loadoutService = loadoutService;
    }

    @EventHandler
    public void onStart(DuelStartFightEvent event) {
        DuelFightModel fightModel = event.getDuelFightModel();
        ArenaModel arena = fightModel.getArenaModel();
        if (!config.enabled() || !arena.isBedwars()) {
            return;
        }
        BedwarsTeam senderTeam = new BedwarsTeam(TeamSide.SENDER, members(fightModel, TeamSide.SENDER),
                arena.getOnePosition(), Color.RED);
        BedwarsTeam receiverTeam = new BedwarsTeam(TeamSide.RECEIVER, members(fightModel, TeamSide.RECEIVER),
                arena.getTwoPosition(), Color.AQUA);
        BedwarsGame game = new BedwarsGame(fightModel, senderTeam, receiverTeam);
        gameBucket.add(game);

        generatorService.buildGenerators(game, arena);
        generatorService.startGenerators(game);

        equipTeam(game, senderTeam);
        equipTeam(game, receiverTeam);
    }

    private void equipTeam(BedwarsGame game, BedwarsTeam team) {
        for (UUID uuid : team.getMembers()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                loadoutService.give(game, team, player);
            }
        }
    }

    // HIGH so it overrides the arena BlockListener (bedwars arenas allow building via breaking:true).
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        BedwarsGame game = gameBucket.byPlayer(player.getUniqueId());
        if (game == null) {
            return;
        }
        // Protect generator marker blocks.
        if (config.generators().containsKey(event.getBlock().getType().name())) {
            event.setCancelled(true);
            return;
        }
        if (!Tag.BEDS.isTagged(event.getBlock().getType())) {
            return;
        }
        BedwarsTeam breakerTeam = game.teamOf(player.getUniqueId());
        TeamSide ownerSide = nearestSide(game, event.getBlock().getLocation());
        if (breakerTeam != null && breakerTeam.getSide() == ownerSide) {
            event.setCancelled(true);
            messages.sendTo(player, "bedwars-own-bed");
            return;
        }
        event.setCancelled(false);
        game.setBedAlive(ownerSide, false);
        Component message = messages.message(Placeholder.wrapped("(player)", player.getName()), "bedwars-bed-destroyed");
        onlineMembers(game).forEach(p -> p.sendMessage(message));
    }

    // LOWEST so we fully own death handling before the default round logic runs.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onKill(DuelKillPlayerEvent event) {
        DuelFightModel fightModel = event.getDuelFightModel();
        BedwarsGame game = fightModel == null ? null : gameBucket.byFight(fightModel);
        if (game == null) {
            return;
        }
        // Bedwars owns the whole outcome — the default round handler must not run.
        event.setCancelled(true);

        DuelPlayer victim = event.getVictim();
        DuelPlayer killer = event.getKiller();
        BedwarsTeam team = game.teamOf(victim.getUUID());
        if (team == null) {
            return;
        }
        if (killer != null && !killer.getUUID().equals(victim.getUUID())) {
            killer.addKill();
        }
        victim.addDeath();

        if (game.bedAlive(team.getSide())) {
            respawn(game, team, victim);
            return;
        }
        eliminate(game, team, victim);
    }

    private void respawn(BedwarsGame game, BedwarsTeam team, DuelPlayer victim) {
        Player immediate = BukkitAdapter.adapt(victim);
        if (immediate != null) {
            immediate.setHealth(immediate.getMaxHealth());
        }
        EntityPosition spawn = team.getSpawn();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Player player = BukkitAdapter.adapt(victim);
            if (player == null || !player.isOnline()) {
                return;
            }
            player.setHealth(player.getMaxHealth());
            player.setFireTicks(0);
            victim.setGameMode(GameMode.SURVIVAL);
            if (spawn != null) {
                victim.teleport(spawn);
            }
            loadoutService.give(game, team, player);
            messages.sendTo(victim, "bedwars-respawn");
        });
    }

    private void eliminate(BedwarsGame game, BedwarsTeam team, DuelPlayer victim) {
        victim.setGameMode(GameMode.SPECTATOR);
        EntityPosition spectator = game.getFightModel().getArenaModel().getSpectatorPosition();
        if (spectator != null) {
            victim.teleport(spectator);
        }
        messages.sendTo(victim, "bedwars-eliminated");

        if (isTeamEliminated(team)) {
            TeamSide winnerSide = team.getSide().opposite();
            DuelPlayer winner = representative(game.getFightModel(), winnerSide);
            DuelPlayer loser = representative(game.getFightModel(), team.getSide());
            plugin.getServer().getScheduler().runTask(plugin, () -> duelAPI.stopFight(game.getFightModel(), winner, loser));
        }
    }

    @EventHandler
    public void onStop(DuelStopFightEvent event) {
        BedwarsGame game = gameBucket.byFight(event.getDuelFightModel());
        if (game == null) {
            return;
        }
        generatorService.stopGenerators(game);
        gameBucket.remove(game);
    }

    private boolean isTeamEliminated(BedwarsTeam team) {
        for (UUID uuid : team.getMembers()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                return false;
            }
        }
        return true;
    }

    private DuelPlayer representative(DuelFightModel fightModel, TeamSide side) {
        return side == TeamSide.SENDER ? fightModel.getSender() : fightModel.getReceiver();
    }

    private TeamSide nearestSide(BedwarsGame game, Location bed) {
        EntityPosition one = game.getSenderTeam().getSpawn();
        EntityPosition two = game.getReceiverTeam().getSpawn();
        if (one == null || two == null) {
            return TeamSide.SENDER;
        }
        return squared(bed, one) <= squared(bed, two) ? TeamSide.SENDER : TeamSide.RECEIVER;
    }

    private double squared(Location location, EntityPosition position) {
        double dx = location.getX() - position.getX();
        double dy = location.getY() - position.getY();
        double dz = location.getZ() - position.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private List<Player> onlineMembers(BedwarsGame game) {
        List<Player> players = new ArrayList<>();
        addMembers(players, game.getSenderTeam());
        addMembers(players, game.getReceiverTeam());
        return players;
    }

    private void addMembers(List<Player> players, BedwarsTeam team) {
        for (UUID uuid : team.getMembers()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                players.add(player);
            }
        }
    }

    private List<UUID> members(DuelFightModel fightModel, TeamSide side) {
        List<UUID> members = new ArrayList<>();
        if (side == TeamSide.SENDER) {
            PartyModel party = fightModel.getSenderParty();
            if (party != null) {
                members.addAll(party.getPlayers());
                members.add(party.getOwner());
            } else {
                if (fightModel.getSender() != null) members.add(fightModel.getSender().getUUID());
                if (fightModel.getPlayer2() != null) members.add(fightModel.getPlayer2().getUUID());
            }
        } else {
            PartyModel party = fightModel.getReceiverParty();
            if (party != null) {
                members.addAll(party.getPlayers());
                members.add(party.getOwner());
            } else {
                if (fightModel.getReceiver() != null) members.add(fightModel.getReceiver().getUUID());
                if (fightModel.getPlayer4() != null) members.add(fightModel.getPlayer4().getUUID());
            }
        }
        return members;
    }

}

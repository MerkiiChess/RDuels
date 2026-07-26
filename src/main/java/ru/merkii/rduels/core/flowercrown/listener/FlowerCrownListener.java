package ru.merkii.rduels.core.flowercrown.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.bedwars.game.TeamSide;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.event.DuelStartFightEvent;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.flowercrown.bucket.FlowerCrownGameBucket;
import ru.merkii.rduels.core.flowercrown.config.FlowerCrownConfiguration;
import ru.merkii.rduels.core.flowercrown.game.FlowerCrownGame;
import ru.merkii.rduels.model.EntityPosition;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Flower Crown: each side owns the flowers nearest its spawn. Breaking an opponent's
 * flower steals it (a point for the breaker's side); you cannot break your own. The
 * first side to steal {@code flowers-to-win} flowers wins.
 */
@Singleton
public class FlowerCrownListener implements Listener {

    private final RDuels plugin;
    private final DuelAPI duelAPI;
    private final FlowerCrownConfiguration config;
    private final FlowerCrownGameBucket gameBucket;
    private final MessageConfig messages;

    @Inject
    public FlowerCrownListener(RDuels plugin, DuelAPI duelAPI, FlowerCrownConfiguration config,
                               FlowerCrownGameBucket gameBucket, MessageConfig messages) {
        this.plugin = plugin;
        this.duelAPI = duelAPI;
        this.config = config;
        this.gameBucket = gameBucket;
        this.messages = messages;
    }

    @EventHandler
    public void onStart(DuelStartFightEvent event) {
        DuelFightModel fightModel = event.getDuelFightModel();
        if (config.enabled() && fightModel.getArenaModel().isFlowerCrown()) {
            gameBucket.add(new FlowerCrownGame(fightModel));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        FlowerCrownGame game = gameBucket.byPlayer(player.getUniqueId());
        if (game == null || !isFlower(event.getBlock().getType())) {
            return;
        }
        ArenaModel arena = game.getFightModel().getArenaModel();
        TeamSide ownerSide = nearestSide(arena, event.getBlock().getLocation());
        TeamSide breakerSide = sideOf(game.getFightModel(), player.getUniqueId());
        if (breakerSide == null) {
            return;
        }
        if (breakerSide == ownerSide) {
            // Protect your own flowers.
            event.setCancelled(true);
            messages.sendTo(player, "flowercrown-own");
            return;
        }
        // Steal the opponent's flower (allowed even in a no-build arena).
        event.setCancelled(false);
        int score = game.addScore(breakerSide);

        Component message = messages.message(Placeholder.Placeholders.of(
                Placeholder.of("(player)", player.getName()),
                Placeholder.of("(score)", String.valueOf(score)),
                Placeholder.of("(target)", String.valueOf(config.flowersToWin()))
        ), "flowercrown-stolen");
        participants(game.getFightModel()).forEach(p -> p.sendMessage(message));

        if (score >= config.flowersToWin()) {
            DuelPlayer winner = representative(game.getFightModel(), breakerSide);
            DuelPlayer loser = representative(game.getFightModel(), breakerSide.opposite());
            plugin.getServer().getScheduler().runTask(plugin, () -> duelAPI.stopFight(game.getFightModel(), winner, loser));
        }
    }

    @EventHandler
    public void onStop(DuelStopFightEvent event) {
        FlowerCrownGame game = gameBucket.byFight(event.getDuelFightModel());
        if (game != null) {
            gameBucket.remove(game);
        }
    }

    private boolean isFlower(Material material) {
        List<String> configured = config.flowerMaterials();
        if (configured.isEmpty()) {
            return Tag.SMALL_FLOWERS.isTagged(material) || Tag.TALL_FLOWERS.isTagged(material);
        }
        return configured.stream().anyMatch(name -> name.toUpperCase(Locale.ROOT).equals(material.name()));
    }

    private TeamSide nearestSide(ArenaModel arena, Location flower) {
        EntityPosition one = arena.getOnePosition();
        EntityPosition two = arena.getTwoPosition();
        if (one == null || two == null) {
            return TeamSide.SENDER;
        }
        return squared(flower, one) <= squared(flower, two) ? TeamSide.SENDER : TeamSide.RECEIVER;
    }

    private double squared(Location location, EntityPosition position) {
        double dx = location.getX() - position.getX();
        double dz = location.getZ() - position.getZ();
        return dx * dx + dz * dz;
    }

    private TeamSide sideOf(DuelFightModel fightModel, UUID uuid) {
        if (matches(fightModel.getSender(), uuid) || matches(fightModel.getPlayer2(), uuid)
                || (fightModel.getSenderParty() != null && (fightModel.getSenderParty().getOwner().equals(uuid)
                || fightModel.getSenderParty().getPlayers().contains(uuid)))) {
            return TeamSide.SENDER;
        }
        if (matches(fightModel.getReceiver(), uuid) || matches(fightModel.getPlayer4(), uuid)
                || (fightModel.getReceiverParty() != null && (fightModel.getReceiverParty().getOwner().equals(uuid)
                || fightModel.getReceiverParty().getPlayers().contains(uuid)))) {
            return TeamSide.RECEIVER;
        }
        return null;
    }

    private boolean matches(DuelPlayer duelPlayer, UUID uuid) {
        return duelPlayer != null && duelPlayer.getUUID().equals(uuid);
    }

    private DuelPlayer representative(DuelFightModel fightModel, TeamSide side) {
        return side == TeamSide.SENDER ? fightModel.getSender() : fightModel.getReceiver();
    }

    private List<Player> participants(DuelFightModel fightModel) {
        List<Player> players = new java.util.ArrayList<>();
        add(players, fightModel.getSender());
        add(players, fightModel.getReceiver());
        add(players, fightModel.getPlayer2());
        add(players, fightModel.getPlayer4());
        return players;
    }

    private void add(List<Player> players, DuelPlayer duelPlayer) {
        if (duelPlayer == null) {
            return;
        }
        Player player = plugin.getServer().getPlayer(duelPlayer.getUUID());
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }
}

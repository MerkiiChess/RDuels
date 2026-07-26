package ru.merkii.rduels.core.killstreak.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.event.DuelKillPlayerEvent;
import ru.merkii.rduels.core.duel.event.DuelStopFightEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.killstreak.bucket.KillStreakBucket;
import ru.merkii.rduels.core.killstreak.config.KillStreakConfiguration;
import ru.merkii.rduels.core.killstreak.config.KillStreakTier;

@Singleton
public class KillStreakListener implements Listener {

    private final KillStreakBucket bucket;
    private final KillStreakConfiguration config;

    @Inject
    public KillStreakListener(KillStreakBucket bucket, KillStreakConfiguration config) {
        this.bucket = bucket;
        this.config = config;
    }

    @EventHandler
    public void onKill(DuelKillPlayerEvent event) {
        if (!config.enabled() || event.getKiller() == null || event.getVictim() == null) {
            return;
        }
        // Death breaks the victim's streak.
        bucket.reset(event.getVictim().getUUID());

        Player killer = BukkitAdapter.adapt(event.getKiller());
        if (killer == null) {
            return;
        }
        int streak = bucket.increment(killer.getUniqueId());

        config.tiers().stream()
                .filter(tier -> tier.count() == streak)
                .findFirst()
                .ifPresent(tier -> announce(killer, streak, tier, event.getDuelFightModel()));
    }

    private void announce(Player killer, int streak, KillStreakTier tier, DuelFightModel fightModel) {
        String raw = tier.message() == null ? "" : tier.message();
        raw = raw.replace("(player)", killer.getName()).replace("(streak)", String.valueOf(streak));
        Component message = MiniMessage.miniMessage().deserialize(raw);

        if (config.broadcastGlobal()) {
            Bukkit.getServer().sendMessage(message);
        } else {
            fightParticipants(fightModel).forEach(player -> player.sendMessage(message));
        }
        tier.commands().forEach(command -> command.execute(killer));
    }

    private java.util.List<Player> fightParticipants(DuelFightModel fightModel) {
        java.util.List<Player> players = new java.util.ArrayList<>();
        addIfOnline(players, fightModel.getSender());
        addIfOnline(players, fightModel.getReceiver());
        addIfOnline(players, fightModel.getPlayer2());
        addIfOnline(players, fightModel.getPlayer4());
        if (fightModel.getSenderParty() != null) {
            fightModel.getSenderParty().getPlayers().forEach(uuid -> addOnline(players, uuid));
            addOnline(players, fightModel.getSenderParty().getOwner());
        }
        if (fightModel.getReceiverParty() != null) {
            fightModel.getReceiverParty().getPlayers().forEach(uuid -> addOnline(players, uuid));
            addOnline(players, fightModel.getReceiverParty().getOwner());
        }
        return players;
    }

    private void addIfOnline(java.util.List<Player> players, DuelPlayer duelPlayer) {
        if (duelPlayer != null) {
            addOnline(players, duelPlayer.getUUID());
        }
    }

    private void addOnline(java.util.List<Player> players, java.util.UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }

    @EventHandler
    public void onStopFight(DuelStopFightEvent event) {
        DuelFightModel fightModel = event.getDuelFightModel();
        fightParticipants(fightModel).forEach(player -> bucket.reset(player.getUniqueId()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        bucket.reset(event.getPlayer().getUniqueId());
    }

}

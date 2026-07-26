package ru.merkii.rduels.core.killmessage.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.killmessage.config.KillMessageConfiguration;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Replaces the vanilla death message of duel participants with a configurable,
 * cause-specific message (bow, void, fall, …), shown to the fight participants or
 * broadcast globally.
 */
@Singleton
public class KillMessageListener implements Listener {

    private final DuelAPI duelAPI;
    private final KillMessageConfiguration config;

    @Inject
    public KillMessageListener(DuelAPI duelAPI, KillMessageConfiguration config) {
        this.duelAPI = duelAPI;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        DuelPlayer duelVictim = BukkitAdapter.adapt(victim);
        DuelFightModel fightModel = duelAPI.getFightModelFromPlayer(duelVictim);
        if (!config.enabled() || fightModel == null) {
            return;
        }
        // Suppress the vanilla death line — we send our own.
        event.deathMessage(null);

        String template = resolveTemplate(victim);
        String killerName = resolveKillerName(victim);
        String rendered = template
                .replace("(victim)", victim.getName())
                .replace("(killer)", killerName);
        Component message = MiniMessage.miniMessage().deserialize(rendered);

        if (config.broadcastGlobal()) {
            Bukkit.getServer().sendMessage(message);
        } else {
            participants(fightModel).forEach(player -> player.sendMessage(message));
        }
    }

    private String resolveTemplate(Player victim) {
        EntityDamageEvent lastCause = victim.getLastDamageCause();
        if (lastCause != null) {
            String key = lastCause.getCause().name().toLowerCase();
            String template = config.causes().get(key);
            if (template != null) {
                return template;
            }
        }
        return config.defaultMessage();
    }

    private String resolveKillerName(Player victim) {
        Player killer = victim.getKiller();
        return killer != null ? killer.getName() : config.environmentName();
    }

    private List<Player> participants(DuelFightModel fightModel) {
        List<Player> players = new ArrayList<>();
        addOnline(players, fightModel.getSender());
        addOnline(players, fightModel.getReceiver());
        addOnline(players, fightModel.getPlayer2());
        addOnline(players, fightModel.getPlayer4());
        if (fightModel.getSenderParty() != null) {
            PlayerUtil.convertListUUID(fightModel.getSenderParty().getPlayers()).forEach(players::add);
            addBukkit(players, fightModel.getSenderParty().getOwner());
        }
        if (fightModel.getReceiverParty() != null) {
            PlayerUtil.convertListUUID(fightModel.getReceiverParty().getPlayers()).forEach(players::add);
            addBukkit(players, fightModel.getReceiverParty().getOwner());
        }
        return players;
    }

    private void addOnline(List<Player> players, DuelPlayer duelPlayer) {
        if (duelPlayer != null) {
            addBukkit(players, duelPlayer.getUUID());
        }
    }

    private void addBukkit(List<Player> players, java.util.UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }

}

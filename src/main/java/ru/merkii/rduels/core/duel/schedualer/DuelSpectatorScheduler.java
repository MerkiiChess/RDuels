package ru.merkii.rduels.core.duel.schedualer;

import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;

public class DuelSpectatorScheduler extends BukkitRunnable {

    public DuelSpectatorScheduler() {
        runTaskTimer(RDuels.getInstance(), 20L, 20L);
    }

    public static DuelSpectatorScheduler start() {
        return new DuelSpectatorScheduler();
    }

    public void stop() {
        this.cancel();
    }

    @Override
    public void run() {
        DuelAPI duelAPI = RDuels.beanScope().get(DuelAPI.class);
        MessageConfig messageConfig = RDuels.beanScope().get(MessageConfig.class);
        RDuels.getInstance().getServer().getOnlinePlayers()
                .stream()
                .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                .filter(player -> {
                    DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
                    return duelAPI.isSpectate(duelPlayer);
                })
                .forEach(player -> {
            player.sendActionBar(messageConfig.message("spectate-leave"));
        });
    }
}

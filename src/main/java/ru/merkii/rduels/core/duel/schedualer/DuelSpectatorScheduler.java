package ru.merkii.rduels.core.duel.schedualer;

import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.duel.DuelCore;

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
        RDuels.getInstance().getServer().getOnlinePlayers().stream().filter(player -> player.getGameMode() == GameMode.SPECTATOR).filter(player -> DuelCore.INSTANCE.getDuelAPI().isSpectate(player)).forEach(player -> {
            player.sendActionBar(RDuels.getInstance().getPluginMessage().getMessage("spectateLeave"));
        });
    }
}

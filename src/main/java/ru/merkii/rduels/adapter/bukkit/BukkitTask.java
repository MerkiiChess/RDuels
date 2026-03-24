package ru.merkii.rduels.adapter.bukkit;

import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.Task;

@Singleton
public class BukkitTask implements Task {

    @Override
    public void syncDelay(Runnable runnable, long ticks) {
        Bukkit.getScheduler().runTaskLater(RDuels.getInstance(), runnable, ticks);
    }
}

package ru.merkii.rduels.core.queue.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.queue.api.QueueAPI;
import ru.merkii.rduels.core.queue.menu.QueueMenu;
import ru.merkii.rduels.core.world.WorldRestrictionService;

@Singleton
@Command({"queue", "q"})
public class QueueGUICommand {

    private final QueueAPI queueAPI;
    private final WorldRestrictionService worldRestrictionService;

    @Inject
    public QueueGUICommand(QueueAPI queueAPI, WorldRestrictionService worldRestrictionService) {
        this.queueAPI = queueAPI;
        this.worldRestrictionService = worldRestrictionService;
    }

    @Command({"queue", "q"})
    public void onQueue(Player player) {
        if (worldRestrictionService.denyIfDisabled(player)) {
            return;
        }
        new QueueMenu().open(player);
    }

    @Command({"queue leave", "q leave"})
    public void onLeave(Player player) {
        queueAPI.leaveQueue(BukkitAdapter.adapt(player));
    }

}

package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.queue.api.QueueAPI;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.model.KitModel;

/**
 * Joins the clicked kit's queue. Registered twice — once per mode — so menu items can
 * map left click to the unranked queue and right click to the ranked one.
 */
public class JoinQueueClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME_UNRANKED = "JOIN_QUEUE_UNRANKED";
    public static final String NAME_RANKED = "JOIN_QUEUE_RANKED";

    private final QueueAPI queueAPI;
    private final boolean ranked;

    public JoinQueueClickHandler(QueueAPI queueAPI, boolean ranked) {
        this.queueAPI = queueAPI;
        this.ranked = ranked;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        Object model = context.get("model").orElse(null);
        if (!(model instanceof KitModel kitModel)) {
            return;
        }
        player.closeInventory();
        queueAPI.joinQueue(BukkitAdapter.adapt(player), kitModel, ranked);
    }
}

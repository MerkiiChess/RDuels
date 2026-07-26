package ru.merkii.rduels.gui.page;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;
import ru.merkii.rduels.model.KitModel;

import java.util.Comparator;
import java.util.List;

/** Lists the server kits (kits.yml) for the queue menu, ordered by their configured slot. */
public class ServerKitsPageResolver implements PageResolver<KitModel> {

    private final KitConfiguration kitConfiguration;

    public ServerKitsPageResolver(KitConfiguration kitConfiguration) {
        this.kitConfiguration = kitConfiguration;
    }

    @Override
    public List<KitModel> resolve(Player player, InventoryContext context) {
        return kitConfiguration.kits().keySet().stream()
                .sorted(Comparator.comparingInt(KitModel::getSlot))
                .toList();
    }
}

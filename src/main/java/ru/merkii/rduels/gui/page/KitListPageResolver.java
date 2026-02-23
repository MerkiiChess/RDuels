package ru.merkii.rduels.gui.page;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.settings.CreateMenuSettings;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;

import java.util.List;
import java.util.stream.Collectors;

public class KitListPageResolver implements PageResolver<CustomKitModel> {

    private final CreateMenuSettings settings;

    public KitListPageResolver(CreateMenuSettings settings) {
        this.settings = settings;
    }

    @Override
    public List<CustomKitModel> resolve(Player player, InventoryContext context) {
        List<CustomKitModel> allKits = settings.kits();

        return allKits.stream()
                .filter(model -> !model.isInvisible() || model.getPermission() == null || player.hasPermission(model.getPermission()))
                .collect(Collectors.toList());
    }
}

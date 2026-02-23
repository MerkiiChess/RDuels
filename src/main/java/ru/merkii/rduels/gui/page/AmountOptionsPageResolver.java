package ru.merkii.rduels.gui.page;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.merkii.rduels.config.settings.ItemConfiguration;
import ru.merkii.rduels.core.customkit.config.CustomKitConfiguration;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;
import ru.merkii.rduels.model.AmountModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AmountOptionsPageResolver implements PageResolver<AmountModel> {

    private final CustomKitConfiguration config;

    public AmountOptionsPageResolver(CustomKitConfiguration config) {
        this.config = config;
    }

    @Override
    public List<AmountModel> resolve(Player player, InventoryContext context) {
        Material material = context.require("material");
        Collection<ItemConfiguration> items = config.categoriesMenu().itemStacks().values();
        return items.stream()
                .filter(builder -> builder.build().getMaxStackSize() >= builder.amount() && !builder.material().contains("POTION"))
                .map(builder -> new AmountModel(builder.amount(), material))
                .collect(Collectors.toList());
    }
}
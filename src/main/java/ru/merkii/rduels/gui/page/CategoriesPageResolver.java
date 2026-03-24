package ru.merkii.rduels.gui.page;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.category.CategoryItemConfiguration;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record CategoriesPageResolver(
        CategoryItemConfiguration categoryItemConfiguration) implements PageResolver<CustomKitCategory> {

    @Override
    public List<CustomKitCategory> resolve(Player player, InventoryContext context) {
        Optional<CustomKitCategory> categoryOptional = context.get("category");
        Collection<CustomKitCategory> categories = categoryItemConfiguration.categories().values();
        if (categoryOptional.isEmpty()) {
            return new ArrayList<>(categories);
        }
        CustomKitCategory category = categoryOptional.get();
        return categories.stream()
                .filter(cat -> cat.getId() != null)
                .filter(cat -> cat.getId().equalsIgnoreCase(category.getId()))
                .toList();
    }

}

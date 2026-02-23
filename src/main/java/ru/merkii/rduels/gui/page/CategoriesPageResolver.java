package ru.merkii.rduels.gui.page;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.settings.CreateMenuSettings;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;
import java.util.List;
import java.util.Optional;

public class CategoriesPageResolver implements PageResolver<CustomKitCategory> {

    private final CreateMenuSettings settings;

    public CategoriesPageResolver(CreateMenuSettings settings) {
        this.settings = settings;
    }

    @Override
    public List<CustomKitCategory> resolve(Player player, InventoryContext context) {
        Optional<CustomKitCategory> categoryOptional = context.get("category");
        List<CustomKitCategory> categories = settings.categories();
        if (categoryOptional.isEmpty()) {
            return categories;
        }
        CustomKitCategory category = categoryOptional.get();
        return categories.stream()
                .filter(cat -> cat.getId() != null)
                .filter(cat -> cat.getId().equalsIgnoreCase(category.getId()))
                .toList();
    }

}

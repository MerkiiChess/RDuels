package ru.merkii.rduels.gui.click;

import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import ru.merkii.rduels.config.category.CategoryItemConfiguration;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.model.SlotModel;

import java.util.Collection;

public record OpenCategoryClickHandler(MenuConfiguration config, InventoryGUIFactory factory,
                                       CategoryItemConfiguration categoryItemConfiguration) implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "OPEN_CATEGORY";

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        Object model = context.get("model").orElse(null);
        String categoryId = null;
        if (model instanceof SlotModel slotModel) {
            categoryId = slotModel.categoryId();
        } else {
            categoryId = context.get("category-id").map(Object::toString).orElse(null);
        }
        context.overrideOrCreate("clicked_slot", context.require("slot"));
        if (categoryId == null || categoryId.equalsIgnoreCase("all")) {
            factory.create("category-menu", player, context.copy())
                    .ifPresent(InventoryGUI::open);
            config.settings().notification().playSound(player, "edit-item");
            return;
        }

        CustomKitCategory category = findCategoryById(categoryId, categoryItemConfiguration.categories().values());
        if (category == null) return;

        InventoryContext newContext = context.copy();
        newContext.overrideOrCreate("category", category);
        factory.create("category-menu", player, newContext)
                .ifPresent(InventoryGUI::open);
        config.settings().notification().playSound(player, "edit-item");
    }

    private CustomKitCategory findCategoryById(String categoryId, Collection<CustomKitCategory> categories) {
        return categories.stream()
                .filter(c -> c.getId() != null && c.getId().equalsIgnoreCase(categoryId))
                .findFirst()
                .orElse(null);
    }
}

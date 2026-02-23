package ru.merkii.rduels.gui.click;

import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.List;

public class OpenCategoryClickHandler implements ClickHandlerRegistry.ClickHandlerFacade{

    public static final String NAME = "OPEN_CATEGORY";
    private final MenuConfiguration config;
    private final InventoryGUIFactory factory;

    @Inject
    public OpenCategoryClickHandler(MenuConfiguration config, InventoryGUIFactory factory) {
        this.config = config;
        this.factory = factory;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        String categoryId = context.get("category-id").map(Object::toString).orElse(null);
        context.extend("clicked_slot", context.require("slot"));
        if (categoryId == null || categoryId.equalsIgnoreCase("all")) {
            InventoryContext newContext = context.copy();
            factory.create("category-menu", player, newContext)
                    .ifPresent(InventoryGUI::open);
            config.settings().notification().playSound(player, "edit-item");
            return;
        }

        CustomKitCategory category = findCategoryById(categoryId, config.settings().createSettings().categories());
        if (category == null) return;

        InventoryContext newContext = context.copy();
        newContext.extend("category", category);
        factory.create("category-menu", player, newContext)
                .ifPresent(InventoryGUI::open);

        config.settings().notification().playSound(player, "edit-item");
    }

    private CustomKitCategory findCategoryById(String categoryId, List<CustomKitCategory> categories) {
        return categories.stream()
                .filter(c -> c.getId() != null && c.getId().equalsIgnoreCase(categoryId))
                .findFirst()
                .orElse(null);
    }
}

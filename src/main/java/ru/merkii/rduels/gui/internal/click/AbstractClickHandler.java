package ru.merkii.rduels.gui.internal.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.Optional;

public abstract class AbstractClickHandler implements ClickHandler {

    private final InventoryContext context;

    public AbstractClickHandler(InventoryContext context) {
        this.context = context;
    }

    protected InventoryContext context() {
        return context;
    }

    protected Player player() {
        return requireArg(ClickHandler.PLAYER);
    }

    protected InventoryItem itemConfig() {
        return requireArg(ClickHandler.ITEM_CONFIG);
    }

    public <T> T requireArg(String key) {
        return context.require(key);
    }

    public <T> Optional<T> arg(String key) {
        return context.get(key);
    }

}

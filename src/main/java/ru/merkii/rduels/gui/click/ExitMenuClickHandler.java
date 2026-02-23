package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public class ExitMenuClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "EXIT_MENU";
    private final MenuConfiguration config;

    public ExitMenuClickHandler(MenuConfiguration config) {
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        config.settings().notification().playSound(player, "exit");
        config.messages().sendTo(player, "exit-menu");
        player.closeInventory();
    }
}

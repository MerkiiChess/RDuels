package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class SelectKitClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "SELECT_KIT";
    private final MenuConfiguration config;

    public SelectKitClickHandler(MenuConfiguration config) {
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        CustomKitModel model = context.require("model");

        String kitName = model.getDisplayName();
        if (kitName == null) {
            return;
        }
        CustomKitAPI customKitAPI = RDuels.beanScope().get(CustomKitAPI.class);
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if (customKitAPI.isSelectedKit(duelPlayer, kitName)) {
            return;
        }

        customKitAPI.setKit(duelPlayer, kitName);

        config.settings().notification().playSound(player, "select-kit");
        config.messages().sendTo(player, Placeholder.wrapped("%kit_name%", kitName), "kit-selected");

        Gui gui = context.require("gui");
        for (int i = 0; i < gui.getHeight() * gui.getWidth(); i++) {
            AbstractItem abstractItem = (AbstractItem) gui.getItem(i);
            if (abstractItem != null) {
                abstractItem.notifyWindows();
            }
        }
    }
}

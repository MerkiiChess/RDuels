package ru.merkii.rduels.gui.invui;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.settings.gui.GuiSettings;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.config.menu.settings.gui.InventorySettings;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.invui.animation.CurtainAnimation;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.window.Window;

import java.util.Arrays;

import static ru.merkii.rduels.gui.invui.BaseInventoryFactory.createItem;


public class BaseInventoryGUI implements InventoryGUI {

    private final Player player;
    private final GuiSettings settings;
    private final Window window;

    public BaseInventoryGUI(Player player, GuiSettings settings, Gui gui, InventoryContext context) {
        this.player = player;
        this.settings = settings;
        this.window = Window.single()
                .setGui(gui)
                .setTitle(new AdventureComponentWrapper(settings.title()))
                .setViewer(player)
                .build();
        context.overrideOrCreate("window", window);

        InventorySettings inventorySettings = settings.inventory();
        inventorySettings.animation().ifPresent(animation -> {
            Material backgroundMaterial = inventorySettings.ingredient(animation.data("background-material").charAt(0)).get().bukkitMaterial();
            InventoryItem curtainMaterial = inventorySettings.ingredient(animation.data("curtain-material").charAt(0)).get();
            CurtainAnimation curtainAnimation = new CurtainAnimation(1, gui, Sound.BLOCK_STONE_PLACE, createItem(player, curtainMaterial, context, null));
            window.addOpenHandler(() -> gui.playAnimation(curtainAnimation, slotElement -> slotElement.getItemStack(null).getType() != backgroundMaterial));
        });
    }

    @Override
    public void open() {
        window.open();
    }

    public Player getPlayer() {
        return player;
    }

    public GuiSettings getSettings() {
        return settings;
    }

    public Window getWindow() {
        return window;
    }

}

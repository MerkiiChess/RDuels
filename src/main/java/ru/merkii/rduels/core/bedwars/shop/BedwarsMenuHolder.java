package ru.merkii.rduels.core.bedwars.shop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ru.merkii.rduels.core.bedwars.game.BedwarsGame;

/** Marker holder for the Bedwars item shop / upgrade shop, carrying the live game. */
public class BedwarsMenuHolder implements InventoryHolder {

    public enum Type { SHOP, UPGRADES }

    private final BedwarsGame game;
    private final Type type;
    private Inventory inventory;

    public BedwarsMenuHolder(BedwarsGame game, Type type) {
        this.game = game;
        this.type = type;
    }

    public BedwarsGame getGame() {
        return game;
    }

    public Type getType() {
        return type;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}

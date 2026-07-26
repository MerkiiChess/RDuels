package ru.merkii.rduels.core.bedwars.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.core.bedwars.shop.BedwarsShopService;

@Singleton
public class BedwarsCommand {

    private final BedwarsShopService shopService;

    @Inject
    public BedwarsCommand(BedwarsShopService shopService) {
        this.shopService = shopService;
    }

    @Command({"shop", "магазин"})
    public void onShop(Player player) {
        shopService.openShop(player);
    }

    @Command({"upgrades", "улучшения"})
    public void onUpgrades(Player player) {
        shopService.openUpgrades(player);
    }
}

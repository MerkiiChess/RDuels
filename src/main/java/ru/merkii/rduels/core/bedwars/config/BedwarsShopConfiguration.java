package ru.merkii.rduels.core.bedwars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface BedwarsShopConfiguration {

    /** Sidebar/menu title (MiniMessage). */
    default String title() {
        return "<green>Магазин";
    }

    List<BedwarsShopItem> items();

}

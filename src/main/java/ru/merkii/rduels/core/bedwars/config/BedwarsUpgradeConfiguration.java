package ru.merkii.rduels.core.bedwars.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;

import java.util.List;

@ConfigInterface
public interface BedwarsUpgradeConfiguration {

    default String title() {
        return "<aqua>Улучшения команды";
    }

    List<BedwarsUpgradeEntry> upgrades();

}

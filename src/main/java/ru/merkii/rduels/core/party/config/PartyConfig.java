package ru.merkii.rduels.core.party.config;

import lombok.Getter;
import org.bukkit.Material;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.settings.Config;

@Getter
public class PartyConfig extends Config {

    private String duration = "5m";
    private int maxPartySize = 10;

    private FightMenu fightMenu = new FightMenu();

    @Getter
    public static class FightMenu {
        private String title = "Кинуть бой";
        private int size = 54;
        private ItemBuilder fight = ItemBuilder.builder().setMaterial("").setDisplayName("Пати: (player)").setLore("Игроки:");
        private ItemBuilder exit = ItemBuilder.builder().setSlot(53).setDisplayName("Выйти из меню").setMaterial(Material.BARRIER);
        private Material fightParty = Material.WITHER_SKELETON_SKULL;
        private Material freeParty = Material.SKELETON_SKULL;
    }

}

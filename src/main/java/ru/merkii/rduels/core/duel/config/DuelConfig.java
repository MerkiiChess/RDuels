package ru.merkii.rduels.core.duel.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.settings.Config;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.model.KitModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class DuelConfig extends Config {

    private int itemRemoveSeconds = 5;
    private ChoiceKitMenu choiceKitMenu = new ChoiceKitMenu();
    private TitleSettings titleSettings = new TitleSettings();

    @Getter
    public static class TitleSettings {

        private ToFight toFight = new ToFight();
        private Fight fight = new Fight();
        private Lose lose = new Lose();
        private Win win = new Win();

        @Getter
        public static class ToFight {
            private int fadeIn = 1;
            private int fadeOut = 1;
            private int stay = 1;
            private String text = "До начала сражения: (time)";
            private String soundName = "ENTITY_PLAYER_ATTACK_CRIT";
            private float v1 = 2.0F;
            private float v2 = 2.0F;
        }

        @Getter
        public static class Fight {
            private int fadeIn = 1;
            private int fadeOut = 1;
            private int stay = 1;
            private String text = "В бой";
            private String soundName = "ENTITY_PLAYER_ATTACK_CRIT";
            private float v1 = 2.0F;
            private float v2 = 2.0F;
        }

        @Getter
        public static class Lose {
            private int fadeIn = 1;
            private int fadeOut = 1;
            private int stay = 1;
            private String text = "Поражение";
            private String soundName = "ENTITY_PLAYER_ATTACK_CRIT";
            private float v1 = 2.0F;
            private float v2 = 2.0F;
        }

        @Getter
        public static class Win {
            private int fadeIn = 1;
            private int fadeOut = 1;
            private int stay = 1;
            private String text = "Победа";
            private String soundName = "ENTITY_PLAYER_ATTACK_CRIT";
            private float v1 = 2.0F;
            private float v2 = 2.0F;
        }

    }

    @Getter
    public static class ChoiceKitMenu {

        private String noSelected = "Не выбрано";
        private ItemBuilder exit = ItemBuilder.builder().setSlot(26).setMaterial(Material.BARRIER).setDisplayName("Выйти").addItemFlags(ItemFlag.values());
        private KitTypeChoice kitTypeChoice = new KitTypeChoice();
        private RequestSettings requestSettings = new RequestSettings();
        private RequestNumGames requestNumGames = new RequestNumGames();
        private RequestKit requestKit = new RequestKit();
        private RequestArena requestArena = new RequestArena();

        @Getter
        public static class KitTypeChoice {
            private int size = 27;
            private String title = "Выбор кита";
            private ItemBuilder serverKit = ItemBuilder.builder().setSlot(11).setMaterial(Material.DIAMOND_SWORD).setDisplayName("Серверные киты").setLore("Здесь присутствуют только те киты,", "которые администрация сама настроила").addItemFlags(ItemFlag.values());
            private ItemBuilder customKit = ItemBuilder.builder().setSlot(14).setMaterial(Material.GOLDEN_SWORD).setDisplayName("Кастомные киты").setLore("Здесь присутствуют только те киты,", "которые вы сами настроили").addItemFlags(ItemFlag.values());
        }

        @Getter
        public static class RequestSettings {
            private int size = 54;
            private String title = "Настройка запроса дуэли игроку (player)";
            private ItemBuilder numGames = ItemBuilder.builder().setSlot(20).setMaterial(Material.PAPER).setDisplayName("Количество игр").setLore("Количество игр: (count)", "Кликните сюда чтобы изменить").addItemFlags(ItemFlag.values());
            private ItemBuilder kit = ItemBuilder.builder().setSlot(22).setMaterial(Material.PAPER).setDisplayName("Выбор кита").setLore("Текущий кит: (kitName)", "Кликните сюда чтобы изменить").addItemFlags(ItemFlag.values());
            private ItemBuilder arena = ItemBuilder.builder().setSlot(24).setMaterial(Material.PAPER).setDisplayName("Выбор арены").setLore("Текущая арена: (arena)", "Кликните сюда чтобы изменить").addItemFlags(ItemFlag.values());
            private ItemBuilder confirm = ItemBuilder.builder().setSlot(49).setMaterial(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("Подтвердить").setLore("Кинуть запрос игроку (player)").addItemFlags(ItemFlag.values());
            private ItemBuilder error = ItemBuilder.builder().setMaterial(Material.BARRIER).setDisplayName("Ошибка").setLore("Настройте полностью дуэль").addItemFlags(ItemFlag.values());
        }

        @Getter
        public static class RequestNumGames {
            private String title = "Настройка количества игр для боя с (player)";
            private int size = 54;
            private Map<ItemBuilder, Integer> countFightNum = fastMap(fastList(ItemBuilder.builder().setSlot(10).setMaterial(Material.PAPER).setDisplayName("1 игра"), ItemBuilder.builder().setSlot(13).setMaterial(Material.PAPER).setAmount(3).setDisplayName("3 игры"), ItemBuilder.builder().setSlot(16).setMaterial(Material.PAPER).setAmount(5).setDisplayName("5 игр")), fastList(1, 3, 5));
        }

        @Getter
        public static class RequestKit {
            private String title = "Настройка кита для боя с (player)";
            private int size = 54;
            private Map<ItemBuilder, KitModel> kits = new HashMap<>();
        }

        @Getter
        public static class RequestArena {
            private String title = "Настройка арены для боя с (player)";
            private int size = 54;
            private ItemBuilder error = ItemBuilder.builder().setMaterial(Material.BARRIER).setDisplayName("ОШИБКА!!").setLore("Все арены данного типа в бою!").addItemFlags(ItemFlag.values());
            private Map<ItemBuilder, String> arenas = new HashMap<>();
        }

    }

    @Override
    public void init() {
        for (KitModel kitModel : RDuels.getInstance().getKitConfig().getKits()) {
            choiceKitMenu.getRequestKit().getKits().put(ItemBuilder.builder().setSlot(kitModel.getSlot()).setMaterial(kitModel.getDisplayMaterial()).setDisplayName(kitModel.getDisplayName()).addItemFlags(ItemFlag.values()).setLore(kitModel.getLore()), kitModel);
        }
        List<String> tempNames = new ArrayList<>();
        for (ArenaModel arenaModel : ArenaCore.INSTANCE.getArenas().getArenas()) {
            if (!tempNames.isEmpty() && tempNames.stream().anyMatch(displayName -> arenaModel.getDisplayName().equalsIgnoreCase(displayName))) {
                continue;
            }
            tempNames.add(arenaModel.getDisplayName());
            this.getChoiceKitMenu().getRequestArena().getArenas().put(ItemBuilder.builder().setSlot(this.getChoiceKitMenu().getRequestArena().getArenas().size()).setMaterial(arenaModel.getMaterial()).setDisplayName(arenaModel.getDisplayName()).addItemFlags(ItemFlag.values()), arenaModel.getDisplayName());
        }
        super.init();
    }
}

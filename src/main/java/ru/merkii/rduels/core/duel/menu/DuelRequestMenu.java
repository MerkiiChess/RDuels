package ru.merkii.rduels.core.duel.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.config.DuelConfig;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.menu.VMenu;
import ru.merkii.rduels.menu.event.ClickEvent;

import java.util.List;

public class DuelRequestMenu extends VMenu {

    private final DuelCore duelCore = DuelCore.INSTANCE;
    private final DuelConfig duelConfig = duelCore.getDuelConfig();
    private final DuelConfig.ChoiceKitMenu choiceKitMenu = duelConfig.getChoiceKitMenu();
    private final DuelRequest duelRequest;
    private final boolean ffa;

    public DuelRequestMenu(DuelRequest duelRequest, String title, int size, List<ItemBuilder> itemBuilders, boolean ffa) {
        super(size, title.replace("(player)", duelRequest.getReceiver().getName()));
        this.duelRequest = duelRequest;
        this.ffa = ffa;
        itemBuilders.forEach(this::setItem);
    }

    @Override
    public void onClick(ClickEvent event) {
        Player player = event.getPlayer();
        ItemBuilder item = event.getItemBuilder();
        // Настройка сколько раз будет бой
        if (this.choiceKitMenu.getRequestNumGames().getCountFightNum().containsKey(item)) {
            this.duelRequest.setNumGames(this.choiceKitMenu.getRequestNumGames().getCountFightNum().get(item));
            new DuelChoiceKitMenu(this.duelRequest, this.ffa).open(player);
            return;
        }
        // Настройка серверного кита
        if (this.choiceKitMenu.getRequestKit().getKits().containsKey(item)) {
            this.duelRequest.setKitModel(this.choiceKitMenu.getRequestKit().getKits().get(item));
            new DuelChoiceKitMenu(this.duelRequest, this.ffa).open(player);
            return;
        }
        // Настройки арены
        if (this.choiceKitMenu.getRequestArena().getArenas().containsKey(item)) {
            ArenaModel arenaModel = this.duelCore.getDuelAPI().getFreeArenaName(this.choiceKitMenu.getRequestArena().getArenas().get(item));
            if (arenaModel == null) {
                inventory.setItem(event.getSlot(), this.choiceKitMenu.getRequestArena().getError().build());
                Bukkit.getScheduler().runTaskLater(RDuels.getInstance(), () -> inventory.setItem(event.getSlot(), item.build()), 45L);
                return;
            }
            this.duelRequest.setArena(this.duelCore.getDuelAPI().getFreeArenaName(this.choiceKitMenu.getRequestArena().getArenas().get(item)));
            new DuelChoiceKitMenu(this.duelRequest, this.ffa).open(player);
        }
    }
}

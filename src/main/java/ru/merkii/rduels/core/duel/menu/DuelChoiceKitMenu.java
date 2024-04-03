package ru.merkii.rduels.core.duel.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.config.DuelConfig;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.menu.VMenu;
import ru.merkii.rduels.menu.event.ClickEvent;
import ru.merkii.rduels.util.ColorUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class DuelChoiceKitMenu extends VMenu {

    private final DuelCore duelCore = DuelCore.INSTANCE;
    private final ArenaAPI arenaAPI = ArenaCore.INSTANCE.getArenaAPI();
    private final DuelConfig duelConfig = duelCore.getDuelConfig();
    private final DuelConfig.ChoiceKitMenu choiceKitConfig = duelConfig.getChoiceKitMenu();
    private final DuelConfig.ChoiceKitMenu.KitTypeChoice kitTypeChoice = choiceKitConfig.getKitTypeChoice();
    private final DuelConfig.ChoiceKitMenu.RequestSettings requestSettings = choiceKitConfig.getRequestSettings();
    private final DuelRequest duelRequest;
    private final boolean ffa;

    public DuelChoiceKitMenu(DuelRequest duelRequest, boolean ffa) {
        this.ffa = ffa;
        this.inventory = duelRequest.getDuelKit() == null ? Bukkit.createInventory(this, this.kitTypeChoice.getSize(), ColorUtil.color(this.kitTypeChoice.getTitle().replace("(player)", duelRequest.getReceiver().getName()))) : Bukkit.createInventory(this, this.requestSettings.getSize(), ColorUtil.color(this.requestSettings.getTitle().replace("(player)", duelRequest.getReceiver().getName())));
        this.duelRequest = duelRequest;
        if (duelRequest.getDuelKit() == null) {
            setItem(this.kitTypeChoice.getCustomKit());
            setItem(this.kitTypeChoice.getServerKit());
        } else {
            setItem(this.requestSettings.getNumGames().clone().replaceLore("(count)", duelRequest.getNumGames() == 0 ? ColorUtil.color(this.choiceKitConfig.getNoSelected()) : String.valueOf(duelRequest.getNumGames())));
            setItem(this.requestSettings.getArena().clone().replaceLore("(arena)", duelRequest.getArena() == null ? ColorUtil.color(this.choiceKitConfig.getNoSelected()) : duelRequest.getArena().getDisplayName()));
            setItem(this.requestSettings.getConfirm().clone().replaceLore("(player)", duelRequest.getReceiver().getDisplayName()));
            if (duelRequest.getDuelKit() == DuelKitType.SERVER) {
                setItem(this.requestSettings.getKit().clone().replaceLore("(kitName)", duelRequest.getKitModel() == null ? ColorUtil.color(this.choiceKitConfig.getNoSelected()) : duelRequest.getKitModel().getDisplayName()));
            }
        }
        setItem(this.choiceKitConfig.getExit());
    }

    @Override
    public void onClick(ClickEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedItem().equals(this.choiceKitConfig.getExit().build())) {
            this.duelCore.getDuelAPI().removeRequest(this.duelRequest);
            player.closeInventory();
            return;
        }
        int slot = event.getSlot();
        if (this.duelRequest.getDuelKit() == null) {
            if (slot == this.kitTypeChoice.getCustomKit().getSlot()) {
                this.duelRequest.setDuelKit(DuelKitType.CUSTOM);
                new DuelChoiceKitMenu(this.duelRequest, this.ffa).open(player);
                return;
            }
            if (slot == this.kitTypeChoice.getServerKit().getSlot()) {
                this.duelRequest.setDuelKit(DuelKitType.SERVER);
                new DuelChoiceKitMenu(this.duelRequest, this.ffa).open(player);
                return;
            }
            return;
        }
        if (slot == this.requestSettings.getNumGames().getSlot()) {
            new DuelRequestMenu(this.duelRequest, this.choiceKitConfig.getRequestNumGames().getTitle(), this.choiceKitConfig.getRequestNumGames().getSize(), new ArrayList<>(this.choiceKitConfig.getRequestNumGames().getCountFightNum().keySet()), this.ffa).open(player);
            return;
        }
        if (slot == this.requestSettings.getArena().getSlot()) {
            Map<ItemBuilder, String> arenas = this.choiceKitConfig.getRequestArena().getArenas();
            new DuelRequestMenu(this.duelRequest, this.choiceKitConfig.getRequestArena().getTitle(), this.choiceKitConfig.getRequestArena().getSize(), new ArrayList<>(this.ffa ? arenas.entrySet().stream().filter(entry -> this.arenaAPI.getArenaFromDisplayName(entry.getValue()).isFfa()).map(Map.Entry::getKey).collect(Collectors.toList()) : arenas.entrySet().stream().filter(entry -> !this.arenaAPI.getArenaFromDisplayName(entry.getValue()).isFfa()).map(Map.Entry::getKey).collect(Collectors.toList())), this.ffa).open(player);
            return;
        }
        if (this.duelRequest.getDuelKit() == DuelKitType.SERVER && slot == this.requestSettings.getKit().getSlot()) {
            new DuelRequestMenu(this.duelRequest, this.choiceKitConfig.getRequestKit().getTitle(), this.choiceKitConfig.getRequestKit().getSize(), new ArrayList<>(this.choiceKitConfig.getRequestKit().getKits().keySet()), this.ffa).open(player);
            return;
        }
        if (slot == this.requestSettings.getConfirm().getSlot()) {
            if (this.duelRequest.getKitModel() == null) {
                if (this.duelRequest.getDuelKit() == DuelKitType.CUSTOM) {
                    this.duelRequest.setKitModel(CustomKitCore.INSTANCE.getCustomKitAPI().getKitModel(player));
                } else {
                    this.duelRequest.setKitModel(this.duelCore.getDuelAPI().getRandomKit());
                }
            }
            if (this.duelRequest.getNumGames() == 0) {
                this.duelRequest.setNumGames(1);
            }
            if (this.duelRequest.getArena() == null) {
                this.duelRequest.setArena(this.duelCore.getDuelAPI().getFreeArena());
            }
            this.duelCore.getDuelAPI().addRequest(this.duelRequest);
            player.closeInventory();
        }
    }
}

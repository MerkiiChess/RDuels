package ru.merkii.rduels.core.duel.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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
import java.util.stream.Stream;

public class DuelChoiceKitMenu extends VMenu {

    private final DuelCore duelCore = DuelCore.INSTANCE;
    private final ArenaAPI arenaAPI = ArenaCore.INSTANCE.getArenaAPI();
    private final DuelConfig duelConfig = this.duelCore.getDuelConfig();
    private final DuelConfig.ChoiceKitMenu choiceKitConfig = this.duelConfig.getChoiceKitMenu();
    private final DuelConfig.ChoiceKitMenu.KitTypeChoice kitTypeChoice = this.choiceKitConfig.getKitTypeChoice();
    private final DuelConfig.ChoiceKitMenu.RequestSettings requestSettings = this.choiceKitConfig.getRequestSettings();
    private final DuelRequest duelRequest;
    private final boolean ffa;

    public DuelChoiceKitMenu(DuelRequest duelRequest, boolean ffa) {
        this.ffa = ffa;
        this.inventory = duelRequest.getDuelKit() == null ? Bukkit.createInventory(this, this.kitTypeChoice.getSize(), ColorUtil.color(this.kitTypeChoice.getTitle().replace("(player)", duelRequest.getReceiver().getName()))) : Bukkit.createInventory(this, this.requestSettings.getSize(), ColorUtil.color(this.requestSettings.getTitle().replace("(player)", duelRequest.getReceiver().getName())));
        this.duelRequest = duelRequest;
        if (duelRequest.getDuelKit() == null) {
            this.setItem(this.kitTypeChoice.getCustomKit());
            this.setItem(this.kitTypeChoice.getServerKit());
        } else {
            this.setItem(this.requestSettings.getNumGames().clone().replaceLore("(count)", duelRequest.getNumGames() == 0 ? ColorUtil.color(this.choiceKitConfig.getNoSelected()) : String.valueOf(duelRequest.getNumGames())));
            this.setItem(this.requestSettings.getArena().clone().replaceLore("(arena)", duelRequest.getArena() == null ? ColorUtil.color(this.choiceKitConfig.getNoSelected()) : duelRequest.getArena().getDisplayName()));
            this.setItem(this.requestSettings.getConfirm().clone().replaceLore("(player)", duelRequest.getReceiver().getDisplayName()));
            if (duelRequest.getDuelKit() == DuelKitType.SERVER) {
                this.setItem(this.requestSettings.getKit().clone().replaceLore("(kitName)", duelRequest.getKitModel() == null ? ColorUtil.color(this.choiceKitConfig.getNoSelected()) : duelRequest.getKitModel().getDisplayName()));
            }
        }
        this.setItem(this.choiceKitConfig.getExit());
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
            new DuelRequestMenu(this.duelRequest, this.choiceKitConfig.getRequestNumGames().getTitle(), this.choiceKitConfig.getRequestNumGames().getSize(), new ArrayList<ItemBuilder>(this.choiceKitConfig.getRequestNumGames().getCountFightNum().keySet()), this.ffa).open(player);
            return;
        }
        if (slot == this.requestSettings.getArena().getSlot()) {
            Stream<Map.Entry<ItemBuilder, String>> d = this.getEntryStream();
            new DuelRequestMenu(this.duelRequest, this.choiceKitConfig.getRequestArena().getTitle(), this.choiceKitConfig.getRequestArena().getSize(), new ArrayList<ItemBuilder>(d.map(Map.Entry::getKey).collect(Collectors.toList())), this.ffa).open(player);
            return;
        }
        if (this.duelRequest.getDuelKit() == DuelKitType.SERVER && slot == this.requestSettings.getKit().getSlot()) {
            new DuelRequestMenu(this.duelRequest, this.choiceKitConfig.getRequestKit().getTitle(), this.choiceKitConfig.getRequestKit().getSize(), new ArrayList<ItemBuilder>(this.choiceKitConfig.getRequestKit().getKits().keySet()), this.ffa).open(player);
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
                if (!this.duelRequest.getKitModel().isBindingArena()) {
                    this.duelRequest.setArena(this.duelCore.getDuelAPI().getFreeArena());
                } else {
                    if (!this.arenaAPI.getArenaFromKit(this.duelRequest.getKitModel()).isPresent()) {
                        player.closeInventory();
                        player.sendMessage(RDuels.getInstance().getPluginMessage().getMessage("duelArenasFull"));
                        return;
                    }
                    this.duelRequest.setArena(this.arenaAPI.getArenaFromKit(this.duelRequest.getKitModel()).get());
                }
            }
            this.duelCore.getDuelAPI().addRequest(this.duelRequest);
            player.closeInventory();
        }
    }

    @NotNull
    private Stream<Map.Entry<ItemBuilder, String>> getEntryStream() {
        Map<ItemBuilder, String> arenas = this.choiceKitConfig.getRequestArena().getArenas();
        Stream<Map.Entry<ItemBuilder, String>> d = this.ffa ? arenas.entrySet().stream().filter(entry -> this.arenaAPI.getArenaFromDisplayName((String)entry.getValue()).isFfa()) : arenas.entrySet().stream().filter(entry -> !this.arenaAPI.getArenaFromDisplayName((String)entry.getValue()).isFfa());
        d = this.duelRequest.getKitModel() != null && this.duelRequest.getKitModel().isBindingArena() ? d.filter(entry -> this.arenaAPI.getArenaFromName((String)entry.getValue()).getCustomKitsName().contains(this.duelRequest.getKitModel().getDisplayName())) : d.filter(entry -> !this.arenaAPI.getArenaFromName((String)entry.getValue()).isCustomKits());
        return d;
    }
}

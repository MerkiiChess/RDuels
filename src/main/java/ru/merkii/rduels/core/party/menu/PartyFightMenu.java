package ru.merkii.rduels.core.party.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import ru.merkii.rduels.core.duel.menu.DuelChoiceKitMenu;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.config.PartyConfig;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.menu.VMenu;
import ru.merkii.rduels.menu.event.ClickEvent;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PartyFightMenu extends VMenu {

    private final PartyCore partyCore = PartyCore.INSTANCE;
    private final PartyConfig partyConfig = partyCore.getPartyConfig();
    private final PartyConfig.FightMenu fightMenu = partyConfig.getFightMenu();
    private final PartyAPI partyAPI = partyCore.getPartyAPI();
    private final Map<Integer, PartyModel> partyModelMap = new HashMap<>();

    public PartyFightMenu() {
        super(PartyCore.INSTANCE.getPartyConfig().getFightMenu().getSize(), PartyCore.INSTANCE.getPartyConfig().getFightMenu().getTitle());
        List<PartyModel> partyModels = this.partyAPI.getAllParty();
        for (int i = 0; i < partyModels.size(); i++) {
            PartyModel partyModel = partyModels.get(i);
            List<String> listPlayers = PlayerUtil.convertListUUID(partyModel.getPlayers()).stream().map(HumanEntity::getName).collect(Collectors.toList());
            if (this.partyAPI.isFightParty(partyModel)) {
                setItem(i, this.fightMenu.getFight().clone().setMaterial(this.fightMenu.getFightParty()).replaceDisplayName("(player)", Bukkit.getPlayer(partyModel.getOwner()).getName()).setLore(listPlayers));
            } else {
                setItem(i, this.fightMenu.getFight().clone().setMaterial(this.fightMenu.getFreeParty()).replaceDisplayName("(player)", Bukkit.getPlayer(partyModel.getOwner()).getName()).setLore(listPlayers));
            }
            this.partyModelMap.put(i, partyModel);
        }
        setItem(this.fightMenu.getExit());
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getItemBuilder().equals(this.fightMenu.getExit())) {
            event.getPlayer().closeInventory();
            return;
        }
        PartyModel partyModel = this.partyModelMap.get(event.getSlot());
        if (partyModel == null || this.partyAPI.isFightParty(partyModel)) {
            return;
        }
        PartyModel clickerModel = this.partyAPI.getPartyModelFromPlayer(event.getPlayer());
        if (clickerModel == null || !clickerModel.getOwner().equals(event.getPlayer().getUniqueId()) || clickerModel.getOwner().equals(partyModel.getOwner())) {
            return;
        }
        new DuelChoiceKitMenu(DuelRequest.create(clickerModel, partyModel), true).open(event.getPlayer());
    }
}

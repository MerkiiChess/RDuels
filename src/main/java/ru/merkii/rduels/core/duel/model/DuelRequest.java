package ru.merkii.rduels.core.duel.model;

import lombok.Getter;
import lombok.Setter;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.model.KitModel;
import ru.merkii.rduels.util.TimeUtil;

import java.util.concurrent.TimeUnit;

@Getter
public class DuelRequest {

    @Setter
    private DuelPlayer sender;
    @Setter
    private DuelPlayer receiver;
    private final long time;
    private final PartyModel senderParty;
    private final PartyModel receiverParty;
    @Setter
    private DuelKitType duelKit;
    @Setter
    private String kitName;
    @Setter
    private int numGames;
    @Setter
    private SignModel signModel;
    @Setter
    private KitModel kitModel;
    @Setter
    private ArenaModel arena;

    public DuelRequest(DuelPlayer sender, DuelPlayer receiver, long time, PartyModel senderParty, PartyModel receiverParty) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.senderParty = senderParty;
        this.receiverParty = receiverParty;
    }

    public static DuelRequest create(DuelPlayer player, DuelPlayer receiver) {
        SettingsConfiguration settingsConfiguration = RDuels.beanScope().get(SettingsConfiguration.class);
        return new DuelRequest(player, receiver, System.currentTimeMillis() + TimeUtil.parseTime(settingsConfiguration.durationRequest(), TimeUnit.MINUTES), null, null);
    }

    public static DuelRequest create(PartyModel senderParty, PartyModel receiverParty) {
        SettingsConfiguration settingsConfiguration = RDuels.beanScope().get(SettingsConfiguration.class);
        return new DuelRequest(BukkitAdapter.getPlayer(senderParty.getOwner()), BukkitAdapter.getPlayer(receiverParty.getOwner()), System.currentTimeMillis() + TimeUtil.parseTime(settingsConfiguration.durationRequest(), TimeUnit.MINUTES), senderParty, receiverParty);
    }
}

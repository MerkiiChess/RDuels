package ru.merkii.rduels.core.duel.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.model.KitModel;
import ru.merkii.rduels.util.TimeUtil;

import java.util.concurrent.TimeUnit;

@Getter
public class DuelRequest {

    @Setter
    private Player sender;
    @Setter
    private Player receiver;
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

    public DuelRequest(Player sender, Player receiver, long time, PartyModel senderParty, PartyModel receiverParty) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.senderParty = senderParty;
        this.receiverParty = receiverParty;
    }

    public static DuelRequest create(Player player, Player receiver) {
        return new DuelRequest(player, receiver, System.currentTimeMillis() + TimeUtil.parseTime(RDuels.getInstance().getSettings().getDurationRequest(), TimeUnit.MINUTES), null, null);
    }

    public static DuelRequest create(PartyModel senderParty, PartyModel receiverParty) {
        return new DuelRequest(Bukkit.getPlayer(senderParty.getOwner()), Bukkit.getPlayer(receiverParty.getOwner()), System.currentTimeMillis() + TimeUtil.parseTime(RDuels.getInstance().getSettings().getDurationRequest(), TimeUnit.MINUTES), senderParty, receiverParty);
    }
}

package ru.merkii.rduels.core.duel.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.model.PartyModel;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DuelStartFightEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    Player sender;
    @Getter
    Player receiver;
    @Getter
    DuelFightModel duelFightModel;
    PartyModel senderParty;
    PartyModel receiverParty;

    public static DuelStartFightEvent create(DuelPlayer sender, DuelPlayer receiver, DuelFightModel duelFightModel) {
        Player bukkitSender = BukkitAdapter.adapt(sender);
        Player bukkitReceiver = BukkitAdapter.adapt(receiver);
        return create(bukkitSender, bukkitReceiver, duelFightModel);
    }

    public static DuelStartFightEvent create(Player sender, Player receiver, DuelFightModel duelFightModel) {
        return new DuelStartFightEvent(sender, receiver, duelFightModel, null, null);
    }

    public static DuelStartFightEvent create(PartyModel senderParty, PartyModel receiverParty, DuelFightModel duelFightModel) {
        return new DuelStartFightEvent(Bukkit.getPlayer(senderParty.getOwner()), Bukkit.getPlayer(receiverParty.getOwner()), duelFightModel, senderParty, receiverParty);
    }

    public void call() {
        RDuels.getInstance().getServer().getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    public PartyModel getSenderParty() {
        return senderParty;
    }

    @Nullable
    public PartyModel getReceiverParty() {
        return receiverParty;
    }
}

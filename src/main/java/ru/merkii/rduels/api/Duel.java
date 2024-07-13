package ru.merkii.rduels.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.api.provider.DuelPlayerProvider;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.sign.SignCore;
import ru.merkii.rduels.core.sign.api.SignAPI;

public class Duel {

    @Nullable
    public static DuelPlayer getDuelPlayer(Player player) {
        if (player == null) {
            return null;
        }
        return new DuelPlayerProvider(player);
    }

    public static DuelAPI getDuelAPI() {
        return DuelCore.INSTANCE.getDuelAPI();
    }

    public static PartyAPI getPartyAPI() {
        return PartyCore.INSTANCE.getPartyAPI();
    }

    public static SignAPI getSignAPI() {
        return SignCore.INSTANCE.getSignAPI();
    }

    public static ArenaAPI getArenaAPI() {
        return ArenaCore.INSTANCE.getArenaAPI();
    }

    public static CustomKitAPI getCustomKitAPI() {
        return CustomKitCore.INSTANCE.getCustomKitAPI();
    }

}

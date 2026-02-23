package ru.merkii.rduels.api;

import io.avaje.inject.BeanScope;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.api.provider.DuelPlayerProvider;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.sign.api.SignAPI;

public class Duel {

    private static final BeanScope BEAN_SCOPE = RDuels.beanScope();

    @Nullable
    public static DuelPlayer getDuelPlayer(Player player) {
        if (player == null) {
            return null;
        }
        return new DuelPlayerProvider(player);
    }

    public static DuelAPI getDuelAPI() {
        return BEAN_SCOPE.get(DuelAPI.class);
    }

    public static PartyAPI getPartyAPI() {
        return BEAN_SCOPE.get(PartyAPI.class);
    }

    public static SignAPI getSignAPI() {
        return BEAN_SCOPE.get(SignAPI.class);
    }

    public static ArenaAPI getArenaAPI() {
        return BEAN_SCOPE.get(ArenaAPI.class);
    }

    public static CustomKitAPI getCustomKitAPI() {
        return BEAN_SCOPE.get(CustomKitAPI.class);
    }

}

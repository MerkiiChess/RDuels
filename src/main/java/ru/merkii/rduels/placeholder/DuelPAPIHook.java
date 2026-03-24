package ru.merkii.rduels.placeholder;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.serializer.ComponentSerializerProviders;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.manager.DatabaseManager;
import ru.merkii.rduels.util.TimeUtil;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DuelPAPIHook extends PlaceholderExpansion {

    MessageConfig messageConfig;
    DuelAPI duelAPI;

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String nullPlaceholder = ComponentSerializerProviders.MINI_MESSAGE.componentSerializer().serialize(messageConfig.message("null-placeholder"));
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        return switch (identifier) {
            case "kills" -> String.valueOf(duelPlayer.getKills());
            case "death" -> String.valueOf(duelPlayer.getDeath());
            case "wins" -> String.valueOf(duelPlayer.getWinRounds());
            case "all_rounds" ->
                    String.valueOf(duelPlayer.getAllRounds() - duelPlayer.getWinRounds());
            case "opponent" ->
                    player == null || !duelPlayer.isFight() ? nullPlaceholder : duelAPI.getOpponentFromFight(duelPlayer).getName();
            case "time" ->
                    player == null || !duelPlayer.isFight() ? nullPlaceholder : TimeUtil.getTimeInMaxUnit(duelPlayer.getDuelFightModel().get().getBukkitTask().getTime() * 1000L);
            case "count_rounds" ->
                    player == null || !duelPlayer.isFight() ? nullPlaceholder : String.valueOf(duelPlayer.getDuelFightModel().get().getNumGames());
            case "played_count_rounds" ->
                    player == null || !duelPlayer.isFight() ? nullPlaceholder : String.valueOf(duelPlayer.getDuelFightModel().get().getCountNumGames());
            case "kit" ->
                    player == null || !duelPlayer.isFight() ? nullPlaceholder : duelPlayer.getDuelFightModel().get().getKitModel().getDisplayName();
            default -> null;
        };
    }


    @Override
    public @NonNull String getAuthor() {
        return "RuMerkii";
    }

    @Override
    public @NonNull String getIdentifier() {
        return "duel";
    }

    @Override
    public @NonNull String getVersion() {
        return "2.0";
    }

}
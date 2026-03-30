package ru.merkii.rduels.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.serializer.ComponentSerializerProviders;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.util.TimeUtil;

import java.util.Optional;

public class DuelPAPIHook extends PlaceholderExpansion {

    private final MessageConfig messageConfig;
    private final DuelAPI duelAPI;

    public DuelPAPIHook(MessageConfig messageConfig, DuelAPI duelAPI) {
        this.messageConfig = messageConfig;
        this.duelAPI = duelAPI;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String nullPlaceholder = ComponentSerializerProviders.MINI_MESSAGE.componentSerializer().serialize(messageConfig.message("null-placeholder"));
        if (player == null) {
            return nullPlaceholder;
        }

        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        Optional<ru.merkii.rduels.core.duel.model.DuelFightModel> duelFightModel = duelPlayer.getDuelFightModel();
        return switch (identifier) {
            case "kills" -> String.valueOf(duelPlayer.getKills());
            case "death" -> String.valueOf(duelPlayer.getDeath());
            case "wins" -> String.valueOf(duelPlayer.getWinRounds());
            case "all_rounds" ->
                    String.valueOf(duelPlayer.getAllRounds() - duelPlayer.getWinRounds());
            case "opponent" -> duelFightModel
                    .map(model -> duelAPI.getOpponentFromFight(model, duelPlayer))
                    .map(DuelPlayer::getName)
                    .orElse(nullPlaceholder);
            case "time" -> duelFightModel
                    .map(model -> TimeUtil.getTimeInMaxUnit(model.getBukkitTask().getTime() * 1000L))
                    .orElse(nullPlaceholder);
            case "count_rounds" -> duelFightModel
                    .map(model -> String.valueOf(model.getNumGames()))
                    .orElse(nullPlaceholder);
            case "played_count_rounds" -> duelFightModel
                    .map(model -> String.valueOf(model.getCountNumGames()))
                    .orElse(nullPlaceholder);
            case "kit" -> duelFightModel
                    .map(model -> model.getKitModel().getDisplayName())
                    .orElse(nullPlaceholder);
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

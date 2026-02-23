package ru.merkii.rduels.core.duel.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.api.Duel;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.lamp.suggestion.AllPlayers;

@Singleton
public class SpectatorCommand {

    private final DuelAPI duelAPI;
    private final MessageConfig config;

    @Inject
    public SpectatorCommand(DuelAPI duelAPI, MessageConfig messageConfig) {
        this.duelAPI = duelAPI;
        this.config = messageConfig;
    }

    @Command({"spec", "spectator", "spec"})
    @Description(value="Присоединиться к наблюдению за дуэлью между игроками.")
    public void onSpec(BukkitCommandActor actor, @SuggestWith(AllPlayers.class) DuelPlayer target) {
        Player bukkitPlayer = actor.requirePlayer();
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);

        if (player.isFight()) {
            config.sendTo(player, "duel-command-is-blocked");
            return;
        }
        String targetName = target.getName();
        if (targetName == null) {
            if (!this.duelAPI.isSpectate(player)) {
                config.sendTo(player, "duel-spec-args");
                return;
            }
            DuelFightModel fightModel = this.duelAPI.getDuelFightModelFromSpectator(player);
            this.duelAPI.removeSpectate(player, fightModel, true);
            config.sendTo(player, "duel-spectate-stop");
            return;
        }
        if (this.duelAPI.isSpectate(player)) {
            DuelFightModel fightModel = this.duelAPI.getDuelFightModelFromSpectator(player);
            this.duelAPI.removeSpectate(player, fightModel, true);
            config.sendTo(player, "duel-spectate-stop");
            return;
        }
        DuelFightModel fightModel = this.duelAPI.getFightModelFromPlayer(target);
        if (fightModel == null || fightModel.getArenaModel().getSpectatorPosition() == null) {
            config.sendTo(player, Placeholder.wrapped("(player)", targetName), "duel-spectate-no-fighting");
            return;
        }
        this.duelAPI.addSpectate(player, fightModel);
        config.sendTo(player, Placeholder.wrapped("(player)", targetName), "duel-spectate-start");
    }

}

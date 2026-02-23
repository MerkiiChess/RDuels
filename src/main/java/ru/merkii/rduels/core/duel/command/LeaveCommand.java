package ru.merkii.rduels.core.duel.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

@Singleton
public class LeaveCommand {

    private final DuelAPI duelAPI;
    private final MessageConfig config;

    @Inject
    public LeaveCommand(DuelAPI duelAPI, MessageConfig messageConfig) {
        this.duelAPI = duelAPI;
        this.config = messageConfig;
    }

    @Command("leave")
    @Description("Выйти с дуэли")
    public void onLeave(BukkitCommandActor actor) {
        DuelPlayer player = BukkitAdapter.adapt(actor.requirePlayer());
        DuelFightModel fightModel = this.duelAPI.getFightModelFromPlayer(player);
        if (!this.duelAPI.isFightPlayer(player) || fightModel == null) {
            config.sendTo(player, "duel-no-fighting");
            return;
        }
        if (player.getGameMode() == GameMode.SPECTATOR || fightModel.isEnd()) {
            config.sendTo(player, "no-stop-fight");
            return;
        }
        if (fightModel.getPlayer2() != null || fightModel.getPlayer4() != null) {
            deleteFromFight(player, fightModel);
            return;
        }
        DuelPlayer winner = this.duelAPI.getWinnerFromFight(fightModel, player);
        this.duelAPI.stopFight(fightModel, winner, this.duelAPI.getLoserFromFight(fightModel, winner));
    }

    private void deleteFromFight(DuelPlayer player, DuelFightModel fightModel) {
        if (fightModel.getSender() != null && fightModel.getSender().equals(player)) {
            fightModel.setSender(null);
        } else if (fightModel.getReceiver() != null && fightModel.getReceiver().equals(player)) {
            fightModel.setReceiver(null);
        } else if (fightModel.getPlayer2() != null && fightModel.getPlayer2().equals(player)) {
            fightModel.setPlayer2(null);
        } else if (fightModel.getPlayer4() != null && fightModel.getPlayer4().equals(player)) {
            fightModel.setPlayer4(null);
        }
        duelAPI.giveStartItems(player);
        player.teleport(duelAPI.getRandomSpawn());
        if (fightModel.getSender() == null && fightModel.getPlayer2() == null) {
            DuelPlayer winner = fightModel.getReceiver() == null ? fightModel.getPlayer4() : fightModel.getReceiver();
            this.duelAPI.stopFight(fightModel, winner, this.duelAPI.getLoserFromFight(fightModel, winner));
            return;
        }
        if (fightModel.getReceiver() == null && fightModel.getPlayer4() == null) {
            DuelPlayer winner = fightModel.getSender() == null ? fightModel.getPlayer2() : fightModel.getSender();
            this.duelAPI.stopFight(fightModel, winner, this.duelAPI.getLoserFromFight(fightModel, winner));
        }
    }

}

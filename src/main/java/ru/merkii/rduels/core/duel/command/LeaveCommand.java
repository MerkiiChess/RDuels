package ru.merkii.rduels.core.duel.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

@CommandAlias("leave")
public class LeaveCommand extends BaseCommand {

    private final DuelAPI duelAPI = DuelCore.INSTANCE.getDuelAPI();
    private final MessageConfiguration messageConfiguration = RDuels.getInstance().getPluginMessage();

    @Default
    @Description("Выйти с дуэли")
    public void onLeave(Player player) {
        DuelFightModel fightModel = this.duelAPI.getFightModelFromPlayer(player);
        if (!this.duelAPI.isFightPlayer(player) || fightModel == null) {
            player.sendMessage(this.messageConfiguration.getMessage("duelNoFighting"));
            return;
        }
        if (player.getGameMode() == GameMode.SPECTATOR || fightModel.isEnd()) {
            player.sendMessage(this.messageConfiguration.getMessage("noStopFight"));
            return;
        }
        if (fightModel.getPlayer2() != null || fightModel.getPlayer4() != null) {
            deleteFromFight(player, fightModel);
            return;
        }
        Player winner = this.duelAPI.getWinnerFromFight(fightModel, player);
        this.duelAPI.stopFight(fightModel, winner, this.duelAPI.getLoserFromFight(fightModel, winner));
    }

    private void deleteFromFight(Player player, DuelFightModel fightModel) {
        if (fightModel.getSender() != null && fightModel.getSender().equals(player)) {
            fightModel.setSender(null);
        } else if (fightModel.getReceiver() != null && fightModel.getReceiver().equals(player)) {
            fightModel.setReceiver(null);
        } else if (fightModel.getPlayer2() != null && fightModel.getPlayer2().equals(player)) {
            fightModel.setPlayer2(null);
        } else if (fightModel.getPlayer4() != null && fightModel.getPlayer4().equals(player)) {
            fightModel.setPlayer4(null);
        }
        if (fightModel.getSender() == null && fightModel.getPlayer2() == null) {
            Player winner = fightModel.getReceiver() == null ? fightModel.getPlayer4() : fightModel.getReceiver();
            this.duelAPI.stopFight(fightModel, winner, this.duelAPI.getLoserFromFight(fightModel, winner));
            return;
        }
        if (fightModel.getReceiver() == null && fightModel.getPlayer4() == null) {
            Player winner = fightModel.getSender() == null ? fightModel.getPlayer2() : fightModel.getSender();
            this.duelAPI.stopFight(fightModel, winner, this.duelAPI.getLoserFromFight(fightModel, winner));
        }
    }

}

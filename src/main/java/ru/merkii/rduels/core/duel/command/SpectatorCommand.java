package ru.merkii.rduels.core.duel.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;

@CommandAlias("spectator|spec")
@Description("Позволяет игроку присоединиться к наблюдению за дуэлью.")
public class SpectatorCommand extends BaseCommand {

    private final RDuels plugin = RDuels.getInstance();
    private final DuelAPI duelAPI = DuelCore.INSTANCE.getDuelAPI();
    private final MessageConfiguration messageConfiguration = this.plugin.getPluginMessage();

    @Default
    @CommandCompletion(value="@allplayers")
    @Description(value="Присоединиться к наблюдению за дуэлью между игроками.")
    public void onSpec(CommandSender sender, @Optional String targetName) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда доступна только в игре.");
            return;
        }
        Player player = (Player) sender;
        if (targetName == null) {
            if (!this.duelAPI.isSpectate(player)) {
                player.sendMessage(this.messageConfiguration.getMessage("duelSpecArgs"));
                return;
            }
            DuelFightModel fightModel = this.duelAPI.getDuelFightModelFromSpectator(player);
            this.duelAPI.removeSpectate(player, fightModel, true);
            player.sendMessage(this.messageConfiguration.getMessage("duelSpectateStop"));
            return;
        }
        if (this.duelAPI.isSpectate(player)) {
            DuelFightModel fightModel = this.duelAPI.getDuelFightModelFromSpectator(player);
            this.duelAPI.removeSpectate(player, fightModel, true);
            player.sendMessage(this.messageConfiguration.getMessage("duelSpectateStop"));
            return;
        }
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            player.sendMessage(this.messageConfiguration.getMessage("duelOffline").replace("(player)", targetName));
            return;
        }
        DuelFightModel fightModel = this.duelAPI.getFightModelFromPlayer(target);
        if (fightModel == null || fightModel.getArenaModel().getSpectatorPosition() == null) {
            player.sendMessage(this.messageConfiguration.getMessage("duelSpectateNoFight").replace("(player)", target.getName()));
            return;
        }
        this.duelAPI.addSpectate(player, fightModel);
        player.sendMessage(this.messageConfiguration.getMessage("duelSpectateStart").replace("(player)", target.getName()));
    }

}

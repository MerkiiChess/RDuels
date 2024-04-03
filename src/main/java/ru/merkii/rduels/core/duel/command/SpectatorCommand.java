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
    private final MessageConfiguration messageConfiguration = plugin.getPluginMessage();

    @Default
    @Syntax("[player]")
    @Description("Присоединиться к наблюдению за дуэлью между игроками.")
    public void onSpec(CommandSender sender, @Optional String targetName) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда доступна только в игре.");
            return;
        }

        Player player = (Player) sender;

        if (targetName == null) {
            if (!duelAPI.isSpectate(player)) {
                player.sendMessage(messageConfiguration.getMessage("duelSpecArgs"));
                return;
            }
            DuelFightModel fightModel = duelAPI.getDuelFightModelFromSpectator(player);
            duelAPI.removeSpectate(player, fightModel, true);
            player.sendMessage(messageConfiguration.getMessage("duelSpectateStop"));
            return;
        }

        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            player.sendMessage(this.messageConfiguration.getMessage("duelOffline").replace("(player)", targetName));
            return;
        }

        if (!duelAPI.isFightPlayer(target)) {
            player.sendMessage(messageConfiguration.getMessage("duelSpectateNoFight").replace("(player)", target.getName()));
            return;
        }

        DuelFightModel fightModel = duelAPI.getFightModelFromPlayer(target);
        if (fightModel == null || fightModel.getArenaModel().getSpectatorPosition() == null) {
            player.sendMessage(messageConfiguration.getMessage("duelSpectateNoFight").replace("(player)", target.getName()));
            return;
        }

        duelAPI.addSpectate(player, fightModel);
        player.sendMessage(messageConfiguration.getMessage("duelSpectateStart").replace("(player)", target.getName()));
    }

}

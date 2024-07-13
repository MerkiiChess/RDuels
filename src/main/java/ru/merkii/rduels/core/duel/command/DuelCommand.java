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
import ru.merkii.rduels.core.duel.menu.DuelChoiceKitMenu;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.util.ColorUtil;
import ru.merkii.rduels.util.TimeUtil;

import java.util.List;

@CommandAlias("duel")
public class DuelCommand extends BaseCommand {

    private final RDuels plugin = RDuels.getInstance();
    private final MessageConfiguration messageConfiguration = plugin.getPluginMessage();
    private final DuelCore duelCore = DuelCore.INSTANCE;
    private final DuelAPI duelAPI = duelCore.getDuelAPI();

    @Default
    @Syntax("<игрок>")
    @CommandCompletion("@allplayers")
    @Description("Вызвать игрока на дуэль.")
    public void onDuel(CommandSender sender, @Name("игрок") String receiverName) {
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage("Эта команда доступна только в игре.");
            return;
        }

        Player receiver = Bukkit.getPlayerExact(receiverName);

        if (receiver == null) {
            senderPlayer.sendMessage(this.messageConfiguration.getMessage("duelOffline").replace("(player)", receiverName));
            return;
        }

        if (senderPlayer.getUniqueId().equals(receiver.getUniqueId())) {
            sender.sendMessage(messageConfiguration.getMessage("duelYou"));
            return;
        }

        if (duelAPI.isFightPlayer(receiver)) {
            sender.sendMessage(messageConfiguration.getMessage("duelAlreadyFighting").replace("(player)", receiver.getName()));
            return;
        }

        DuelRequest duelRequest = duelAPI.getRequestFromSender(senderPlayer, receiver);
        if (duelRequest != null && duelRequest.getTime() > System.currentTimeMillis()) {
            sender.sendMessage(messageConfiguration.getMessage("duelAlreadyRequest").replace("(time)", TimeUtil.getTimeInMaxUnit(duelRequest.getTime() - System.currentTimeMillis())));
            duelAPI.removeRequest(duelRequest);
        }

        new DuelChoiceKitMenu(DuelRequest.create(senderPlayer, receiver), false).open(senderPlayer);
    }

    @Subcommand("yes")
    @Syntax("<игрок>")
    @CommandCompletion("@allplayers")
    @Description("Принять вызов на дуэль.")
    public void onYes(CommandSender sender, @Name("игрок") String senderName) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда доступна только в игре.");
            return;
        }

        Player receiver = (Player) sender;
        Player senderPlayer = Bukkit.getPlayerExact(senderName);

        List<DuelRequest> requests = duelAPI.getRequestsFromReceiver(receiver);
        if (requests == null || requests.isEmpty()) {
            sender.sendMessage(messageConfiguration.getMessage("duelRequestEmpty"));
            return;
        }

        if (senderPlayer == null) {
            sender.sendMessage(messageConfiguration.getMessage("duelOffline").replace("(player)", senderName));
            return;
        }
        DuelRequest request = null;
        for (DuelRequest requestParser : requests) {
            if (requestParser.getSender().getName().equalsIgnoreCase(senderPlayer.getName())) {
                request = requestParser;
                break;
            }
        }

        if (request == null) {
            sender.sendMessage(messageConfiguration.getMessage("duelPlayerNotRequest").replace("(player)", senderPlayer.getName()));
            return;
        }

        duelAPI.removeRequest(request);

        if (duelAPI.isFightPlayer(senderPlayer)) {
            sender.sendMessage(messageConfiguration.getMessage("duelAlreadyFighting").replace("(player)", senderPlayer.getName()));
            return;
        }

        if (request.getTime() < System.currentTimeMillis()) {
            sender.sendMessage(messageConfiguration.getMessage("duelAlreadyFighting").replace("(player)", senderPlayer.getName()));
            return;
        }

        duelAPI.startFight(request);
    }

    @Subcommand("no")
    @Syntax("<игрок>")
    @CommandCompletion("@allplayers")
    @Description("Отклонить вызов на дуэль.")
    public void onNo(CommandSender sender, @Name("игрок") String senderName) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда доступна только в игре.");
            return;
        }

        Player receiver = (Player) sender;

        List<DuelRequest> requests = duelAPI.getRequestsFromReceiver(receiver);
        if (requests == null || requests.isEmpty()) {
            sender.sendMessage(messageConfiguration.getMessage("duelRequestEmpty"));
            return;
        }

        Player senderPlayer = Bukkit.getPlayerExact(senderName);

        if (senderPlayer == null) {
            sender.sendMessage(messageConfiguration.getMessage("duelOffline").replace("(player)", senderName));
            return;
        }
        DuelRequest request = null;
        for (DuelRequest requestParser : requests) {
            if (requestParser.getSender().getName().equalsIgnoreCase(senderPlayer.getName())) {
                request = requestParser;
                break;
            }
        }

        if (request == null) {
            sender.sendMessage(messageConfiguration.getMessage("duelPlayerNotRequest").replace("(player)", senderPlayer.getName()));
            return;
        }

        duelAPI.removeRequest(request);

        if (request.getTime() < System.currentTimeMillis()) {
            sender.sendMessage(messageConfiguration.getMessage("duelRequestTime"));
            return;
        }

        sender.sendMessage(messageConfiguration.getMessage("duelNo"));
        senderPlayer.sendMessage(ColorUtil.color(messageConfiguration.getMessage("duelNoSender").replace("(player)", receiver.getName())));
    }

}

package ru.merkii.rduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;

@CommandAlias("day")
public class DayCommand extends BaseCommand {

    private final RDuels plugin = RDuels.getInstance();
    private final MessageConfiguration messageConfiguration = plugin.getPluginMessage();

    @Default
    @CommandPermission("duel.day")
    @Description("Включить день")
    public void onDay(Player player) {
        this.plugin.getDatabaseManager().setDay(player).join();
        player.setPlayerTime(this.plugin.getSettings().getDayTicks(), false);
        player.sendMessage(this.messageConfiguration.getMessage("day"));
    }

    @CatchUnknown
    public void noPermission(CommandSender sender) {
        sender.sendMessage(this.plugin.getPluginMessage().getMessage("noPermission"));
    }

}

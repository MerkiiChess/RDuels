package ru.merkii.rduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;

@CommandAlias("night")
public class NightCommand extends BaseCommand {

    private final RDuels plugin = RDuels.getInstance();
    private final MessageConfiguration messageConfiguration = plugin.getPluginMessage();

    @Default
    @CommandPermission("duel.night")
    @Description("Включить ночь")
    public void onNight(Player player) {
        this.plugin.getDatabaseManager().setNight(player).join();
        player.setPlayerTime(this.plugin.getSettings().getNightTicks(), false);
        player.sendMessage(this.messageConfiguration.getMessage("night"));
    }

    @CatchUnknown
    public void noPermission(CommandSender sender) {
        sender.sendMessage(this.plugin.getPluginMessage().getMessage("noPermission"));
    }

}

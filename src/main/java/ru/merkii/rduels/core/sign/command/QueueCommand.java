package ru.merkii.rduels.core.sign.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.core.sign.SignCore;
import ru.merkii.rduels.core.sign.api.SignAPI;

@CommandAlias(value="queue")
public class QueueCommand
        extends BaseCommand {
    private final SignAPI signAPI = SignCore.INSTANCE.getSignAPI();
    private final MessageConfiguration messageConfiguration = RDuels.getInstance().getPluginMessage();

    @Default
    public void queue(Player player) {
        if (this.signAPI.isQueuePlayer(player)) {
            this.signAPI.removePlayerQueueSign(player);
        }
    }
}

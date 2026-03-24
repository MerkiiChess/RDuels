package ru.merkii.rduels.core.sign.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.sign.api.SignAPI;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class QueueCommand {

    SignAPI signAPI;
    MessageConfig config;

    @Command("queue")
    private void leavee(Player bukkitPlayer) {
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        if (this.signAPI.isQueuePlayer(player)) {
            this.signAPI.removePlayerQueueSign(player);
            config.sendTo(player, "sign-stop-queue");
            return;
        }
        config.sendTo(player, "sign-no-queue");
    }
}

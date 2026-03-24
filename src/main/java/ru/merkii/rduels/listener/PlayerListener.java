package ru.merkii.rduels.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.duel.matchmaking.DuelMatchmakingService;
import ru.merkii.rduels.manager.DatabaseManager;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayerListener implements Listener {

    DatabaseManager databaseManager;
    SettingsConfiguration settings;
    DuelMatchmakingService matchmakingService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.getInventory().setItem(this.settings.matchmakingItem().slot(), this.settings.matchmakingItem().build());

        if (this.settings.itemOpenCustomKit()) {
            player.getInventory().setItem(this.settings.createCustomKit().slot(), this.settings.createCustomKit().build());
        }

        databaseManager.loadUserData(player.getUniqueId());

        if (databaseManager.isDay(player.getUniqueId())) {
            player.setPlayerTime(this.settings.dayTicks(), false);
        } else if (databaseManager.isNight(player.getUniqueId())) {
            player.setPlayerTime(this.settings.nightTicks(), false);
        }
    }

    @EventHandler
    public void onTryDrop(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (isNotDroppedItem(itemStack)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        leave(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        leave(event.getPlayer());
    }

    private void leave(Player bukkitPlayer) {
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        matchmakingService.leaveQueue(player);

        databaseManager.unloadUserData(bukkitPlayer.getUniqueId());

    }

    private boolean isNotDroppedItem(ItemStack itemStack) {
        return  itemStack.isSimilar(settings.matchmakingItem().build()) ||
                itemStack.isSimilar(settings.createCustomKit().build()) ||
                itemStack.isSimilar(settings.fightParty().build()) ||
                itemStack.isSimilar(settings.leaveParty().build());
    }

}

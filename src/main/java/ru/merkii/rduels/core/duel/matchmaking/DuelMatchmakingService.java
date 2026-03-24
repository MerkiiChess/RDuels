package ru.merkii.rduels.core.duel.matchmaking;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.model.KitModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DuelMatchmakingService {

    Map<KitModel, DuelPlayer> queue = new ConcurrentHashMap<>();
    SettingsConfiguration settingsConfiguration;

    public void addInQueue(DuelPlayer player, KitModel kit) {
        queue.put(kit, player);
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        bukkitPlayer.getInventory().clear();
        bukkitPlayer.getInventory().setItem(settingsConfiguration.matchmakingLeaveItem().slot(), settingsConfiguration.matchmakingLeaveItem().build());
        bukkitPlayer.updateInventory();
    }

    public DuelPlayer getOpponentAndRemove(KitModel kit) {
        return queue.remove(kit);
    }

    public void leaveQueue(DuelPlayer player) {
        queue.values().removeIf(p -> p.getUUID().equals(player.getUUID()));
    }
}
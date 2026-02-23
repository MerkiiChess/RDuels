package ru.merkii.rduels.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class PlayerUtil {

    public static void clearEffects(Player... players) {
        for (Player player : players) {
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
    }

    public static void healPlayers(Player... players) {
        for (Player player : players) {
            player.setHealth(player.getMaxHealth());
            player.setFireTicks(0);
        }
    }

    public static void sendMessage(String text, Player... players) {
        for (Player player : players) {
            player.sendMessage(text);
        }
    }

    public static List<Player> convertListUUID(List<UUID> uuids) {
        return uuids.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<DuelPlayer> duelPlayersConvertListUUID(List<UUID> uuids) {
        return uuids.stream().map(BukkitAdapter::getPlayer).toList();
    }

}

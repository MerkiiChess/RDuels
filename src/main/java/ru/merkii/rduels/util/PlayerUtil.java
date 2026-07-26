package ru.merkii.rduels.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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

    /** Restores the vanilla max-health attribute after kits with a custom max-hearts rule. */
    public static void resetMaxHealth(Player... players) {
        for (Player player : players) {
            AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (maxHealth != null && maxHealth.getBaseValue() != maxHealth.getDefaultValue()) {
                maxHealth.setBaseValue(maxHealth.getDefaultValue());
            }
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
        return uuids.stream()
                .map(BukkitAdapter::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}

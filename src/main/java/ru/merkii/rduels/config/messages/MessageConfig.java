package ru.merkii.rduels.config.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.config.Placeholder;

import java.util.*;

public interface MessageConfig {

    MiniMessage serializer = MiniMessage.miniMessage();

    default Component message(String key, String... keys) {
        return message(Placeholder.Placeholders.empty(), key, keys);
    }

    Component message(Placeholder.Placeholders placeholders, String key, String... keys);

    default String plainMessage(String key, String... keys) {
        return serializer.serialize(message(key, keys));
    }

    MessageConfig sub(String key);

    default void sendTo(Player player, String key, String... keys) {
        player.sendMessage(message(key, keys));
    }

    default void sendTo(Player player, Placeholder.Placeholders placeholders, String key, String... keys) {
        player.sendMessage(message(placeholders, key, keys));
    }

    // test govna

    default void sendTo(DuelPlayer player, String key, String... keys) {
        player.sendMessage(message(key, keys));
    }

    default void sendTo(List<DuelPlayer> players, String key, String... keys) {
        players.forEach(player -> sendTo(player, key, keys));
    }

    default void sendTo(List<DuelPlayer> players, Placeholder.Placeholders placeholders, String key, String... keys) {
        players.forEach(player -> sendTo(player, placeholders, key, keys));
    }

    default void sendTo(DuelPlayer player, Placeholder.Placeholders placeholders, String key, String... keys) {
        player.sendMessage(message(placeholders, key, keys));
    }

}

package ru.merkii.rduels.config.menu.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import ru.merkii.rduels.config.Placeholder;

public interface MessagesMenuConfiguration {

    MiniMessage serializer = MiniMessage.miniMessage();

    default Component message(String key, String... keys) {
        return message(Placeholder.Placeholders.empty(), key, keys);
    }

    Component message(Placeholder.Placeholders placeholders, String key, String... keys);

    default String plainMessage(String key, String... keys) {
        return serializer.serialize(message(key, keys));
    }

    MessagesMenuConfiguration sub(String key);

    default void sendTo(Player player, String key, String... keys) {
        player.sendMessage(message(key, keys));
    }

    default void sendTo(Player player, Placeholder.Placeholders placeholders, String key, String... keys) {
        player.sendMessage(message(placeholders, key, keys));
    }

}

package ru.merkii.rduels.config.menu.settings.notification;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Transient;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

@ConfigInterface
public interface NotificationSettings {

    Map<String, Sound> sounds();

    @Transient
    default Optional<Sound> sound(String key) {
        return Optional.ofNullable(sounds().get(key));
    }

    @Transient
    default void playSound(Player player, String key) {
        sound(key).ifPresent(sound -> player.playSound(player.getLocation(), sound, 1, 1));
    }

}

package ru.merkii.rduels.core.world;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.SettingsConfiguration;

import java.util.Locale;

/**
 * Central check for worlds where the plugin's player-facing commands are disabled
 * (see {@code disabled-worlds} in settings.yml).
 */
@Singleton
public class WorldRestrictionService {

    private final SettingsConfiguration settings;
    private final MessageConfig messages;

    @Inject
    public WorldRestrictionService(SettingsConfiguration settings, MessageConfig messages) {
        this.settings = settings;
        this.messages = messages;
    }

    public boolean isDisabled(Player player) {
        String worldName = player.getWorld().getName().toLowerCase(Locale.ROOT);
        return settings.disabledWorlds().stream()
                .anyMatch(name -> name.toLowerCase(Locale.ROOT).equals(worldName));
    }

    /** Returns true and notifies the player when the plugin is disabled in their world. */
    public boolean denyIfDisabled(Player player) {
        if (isDisabled(player)) {
            messages.sendTo(player, "world-disabled");
            return true;
        }
        return false;
    }

}

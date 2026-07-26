package ru.merkii.rduels.core.party.config;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Setting;
import ru.merkii.rduels.config.settings.ItemConfiguration;

@ConfigInterface
public interface PartyConfiguration {

    int maxPartySize();

    /** Chat line format for party chat; placeholders (player) and (message). */
    default String chatFormat() {
        return "<dark_aqua>[Пати] <gray>(player)<white>: (message)";
    }

    /** Periodically remind party members of the party. */
    default boolean broadcastEnabled() {
        return false;
    }

    default int broadcastIntervalSeconds() {
        return 300;
    }

    default String broadcastMessage() {
        return "<gray>Вы состоите в пати. Напишите <white>/pc <gray>для чата пати.";
    }

}


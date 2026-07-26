package ru.merkii.rduels.core.party.bucket;

import jakarta.inject.Singleton;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Players whose normal chat is currently routed to their party. */
@Singleton
public class PartyChatBucket {

    private final Set<UUID> partyChat = ConcurrentHashMap.newKeySet();

    /** Toggles party-chat mode; returns the new state. */
    public boolean toggle(UUID uuid) {
        if (partyChat.contains(uuid)) {
            partyChat.remove(uuid);
            return false;
        }
        partyChat.add(uuid);
        return true;
    }

    public boolean isEnabled(UUID uuid) {
        return partyChat.contains(uuid);
    }

    public void remove(UUID uuid) {
        partyChat.remove(uuid);
    }

}

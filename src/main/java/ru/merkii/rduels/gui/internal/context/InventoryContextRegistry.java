package ru.merkii.rduels.gui.internal.context;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryContextRegistry {

    Map<UUID, InventoryContext> contexts = new HashMap<>();

    public Optional<InventoryContext> contextOf(Player player) {
        return Optional.ofNullable(contexts.get(player.getUniqueId()));
    }

    public void setContext(Player player, InventoryContext context) {
        requireNonNull(player);
        requireNonNull(context);
        context.extend("player", player);
        contexts.put(player.getUniqueId(), context);
    }

    public void update(InventoryContext context) {
        requireNonNull(context);
        Player player = context.require("player");
        contexts.put(player.getUniqueId(), context);
    }

}

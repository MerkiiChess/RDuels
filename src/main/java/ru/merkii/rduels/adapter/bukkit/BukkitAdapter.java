package ru.merkii.rduels.adapter.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.api.provider.DuelPlayerProvider;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.Position;

import java.util.UUID;

public class BukkitAdapter {

    public static Location adapt(Position position) {
        return new Location(
                Bukkit.getWorld(position.getWorldName()),
                position.getX(),
                position.getY(),
                position.getZ()
        );
    }

    public static EntityPosition adapt(Location location) {
        return new EntityPosition(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getPitch(),
                location.getYaw()
        );
    }

    public static GameMode adapt(ru.merkii.rduels.adapter.bukkit.GameMode gameMode) {
        return GameMode.valueOf(gameMode.name());
    }

    public static DuelPlayer adapt(Player player) {
        return new DuelPlayerProvider(player);
    }

    public static DuelPlayer getPlayer(UUID uuid) {
        return adapt(Bukkit.getPlayer(uuid));
    }

    public static Player adapt(DuelPlayer player) {
        return Bukkit.getPlayer(player.getUUID());
    }

}

package ru.merkii.rduels.core.duel.bucket;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DuelMoveBucket {

    private final List<UUID> dontMoveList = new ArrayList<>();

    public void add(Player player) {
        this.dontMoveList.add(player.getUniqueId());
    }

    public void remove(Player player) {
        this.dontMoveList.remove(player.getUniqueId());
    }

    public boolean contains(Player player) {
        return this.dontMoveList.contains(player.getUniqueId());
    }

}

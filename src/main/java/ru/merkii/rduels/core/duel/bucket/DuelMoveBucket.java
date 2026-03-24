package ru.merkii.rduels.core.duel.bucket;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DuelMoveBucket {

    List<UUID> dontMoveList = new ArrayList<>();

    public void add(DuelPlayer player) {
        this.dontMoveList.add(player.getUUID());
    }

    public void remove(DuelPlayer player) {
        this.dontMoveList.remove(player.getUUID());
    }

    public boolean contains(DuelPlayer player) {
        return this.dontMoveList.contains(player.getUUID());
    }

}

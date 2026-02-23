package ru.merkii.rduels.core.arena.bucket;

import jakarta.inject.Singleton;
import lombok.Getter;
import ru.merkii.rduels.core.arena.model.ArenaModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Singleton
public class ArenaBusyBucket {

    private final List<ArenaModel> arenas = new ArrayList<>();

    public void add(ArenaModel arenaModel) {
        this.arenas.add(arenaModel);
    }

    public void remove(ArenaModel arenaModel) {
        this.arenas.remove(arenaModel);
    }

}

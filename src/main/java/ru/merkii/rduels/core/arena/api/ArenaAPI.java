package ru.merkii.rduels.core.arena.api;

import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.model.KitModel;

import java.util.List;
import java.util.Optional;

public interface ArenaAPI {

    /**
     * Retrieves a free arena available for a fight.
     *
     * @return A free ArenaModel for a fight, or null if none is available.
     */
    @Nullable
    ArenaModel getFreeArena();

    /**
     * Retrieves a free arena with the specified name.
     *
     * @param name The name of the arena to retrieve.
     * @return A free ArenaModel with the specified name, or null if none is available.
     */
    @Nullable
    ArenaModel getFreeArenaName(String name);

    @Nullable
    ArenaModel getFreeArenaFFA();
    /**
     * Retrieves the arena with the specified name.
     *
     * @param name The name of the arena to retrieve.
     * @return The arena model with the specified name, or null if not found.
     */
    @Nullable
    ArenaModel getArenaFromName(String name);

    /**
     * Retrieves the arena with the specified display name.
     *
     * @param displayName The display name of the arena to retrieve.
     * @return The arena model with the specified display name, or null if not found.
     */
    @Nullable
    ArenaModel getArenaFromDisplayName(String displayName);

    /**
     * Checks if the arena with the specified name exists.
     *
     * @param name The name of the arena to check.
     * @return True if the arena with the specified name exists, otherwise false.
     */
    boolean isContainsArena(String name);

    /**
     * Retrieves a list of arenas with the specified name.
     *
     * @param name The name of the arenas to retrieve.
     * @return A list of arenas with the specified name.
     */
    List<ArenaModel> getArenasFromName(String name);

    /**
     * Adds the specified arena to the list of busy arenas.
     *
     * @param arenaModel The arena model to add.
     */
    void addBusyArena(ArenaModel arenaModel);

    /**
     * Removes the specified arena from the list of busy arenas.
     *
     * @param arenaModel The arena model to remove.
     */
    void removeBusyArena(ArenaModel arenaModel);

    boolean isBusyArena(ArenaModel arenaModel);

    /**
     * Restores the specified arena.
     *
     * @param arenaModel The arena model to restore.
     */
    void restoreArena(ArenaModel arenaModel);

    Optional<ArenaModel> getArenaFromKit(KitModel kitModel);

}

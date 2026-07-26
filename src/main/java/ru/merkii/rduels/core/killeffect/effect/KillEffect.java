package ru.merkii.rduels.core.killeffect.effect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * A cosmetic effect played at the moment one duel player kills another.
 * Implementations must be side-effect free with respect to gameplay (no real damage,
 * no block changes) — they are purely visual/audible.
 */
public interface KillEffect {

    /** Machine id referenced from kill-effects.yml and stored as the player's preference. */
    String id();

    /**
     * Plays the effect.
     *
     * @param killer   the player who scored the kill (owner of the effect)
     * @param victim   the player who died (may already be respawning)
     * @param location the victim's death location
     */
    void play(Player killer, Player victim, Location location);

}

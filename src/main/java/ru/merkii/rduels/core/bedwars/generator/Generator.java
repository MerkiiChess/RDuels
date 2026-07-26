package ru.merkii.rduels.core.bedwars.generator;

import org.bukkit.Location;
import ru.merkii.rduels.core.bedwars.game.TeamSide;
import ru.merkii.rduels.core.bedwars.resource.ResourceType;

/**
 * A single resource generator located above a marker block. Team generators (near a
 * base) are sped up by that team's Forge upgrade; central generators (team == null) are
 * not. {@link #countdown} ticks down each generator tick and resets on spawn.
 */
public class Generator {

    private final Location location;
    private final ResourceType resource;
    private final int baseInterval;
    private final TeamSide team;
    private int countdown;

    public Generator(Location location, ResourceType resource, int baseInterval, TeamSide team) {
        this.location = location;
        this.resource = resource;
        this.baseInterval = Math.max(1, baseInterval);
        this.team = team;
        this.countdown = this.baseInterval;
    }

    public Location getLocation() {
        return location;
    }

    public ResourceType getResource() {
        return resource;
    }

    public TeamSide getTeam() {
        return team;
    }

    /**
     * Decrements the countdown, applying a forge speed multiplier for team generators.
     * @return true when a resource should spawn this tick
     */
    public boolean tick(double forgeMultiplier) {
        double step = team != null ? forgeMultiplier : 1.0D;
        countdown -= (int) Math.max(1, Math.round(step));
        if (countdown <= 0) {
            countdown = baseInterval;
            return true;
        }
        return false;
    }
}

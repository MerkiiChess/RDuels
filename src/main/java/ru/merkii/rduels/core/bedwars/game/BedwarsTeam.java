package ru.merkii.rduels.core.bedwars.game;

import org.bukkit.Color;
import ru.merkii.rduels.core.bedwars.upgrade.UpgradeType;
import ru.merkii.rduels.model.EntityPosition;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** One Bedwars team: its members, spawn/bed side, armour colour and purchased upgrades. */
public class BedwarsTeam {

    private final TeamSide side;
    private final List<UUID> members;
    private final EntityPosition spawn;
    private final Color armorColor;
    private final Map<UpgradeType, Integer> upgrades = new EnumMap<>(UpgradeType.class);

    public BedwarsTeam(TeamSide side, List<UUID> members, EntityPosition spawn, Color armorColor) {
        this.side = side;
        this.members = new ArrayList<>(members);
        this.spawn = spawn;
        this.armorColor = armorColor;
    }

    public TeamSide getSide() {
        return side;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public EntityPosition getSpawn() {
        return spawn;
    }

    public Color getArmorColor() {
        return armorColor;
    }

    public int getUpgradeLevel(UpgradeType type) {
        return upgrades.getOrDefault(type, 0);
    }

    public void setUpgradeLevel(UpgradeType type, int level) {
        upgrades.put(type, level);
    }
}

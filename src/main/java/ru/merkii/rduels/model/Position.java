package ru.merkii.rduels.model;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public interface Position extends Cloneable, Comparable<Position> {

    static int locToBlock(double num) {
        int floor = (int)num;
        return (double)floor == num ? floor : floor - (int)(Double.doubleToRawLongBits(num) >>> 63);
    }

    @Override
    default int compareTo(Position other) {
        Preconditions.checkNotNull(other);
        if (this.getY() != other.getY()) {
            return Double.compare(this.getY(), other.getY());
        }
        if (this.getZ() != other.getZ()) {
            return Double.compare(this.getZ(), other.getZ());
        }
        return this.getX() != other.getX() ? Double.compare(this.getX(), other.getX()) : 0;
    }

    double distanceTo(Position var1);

    int getBlockX();

    int getBlockY();

    int getBlockZ();

    double getX();

    double getY();

    double getZ();

    void setX(double var1);

    void setY(double var1);

    void setZ(double var1);

    void setWorld(String var1);

    String getWorldName();

    Location toLocation();

    World getWorld();

    void setWorld(World world);

    Block toBlock();
}

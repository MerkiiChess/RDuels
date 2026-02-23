package ru.merkii.rduels.model;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Objects;

public class BlockPosition implements Position{

    private String world;
    private int x;
    private int y;
    private int z;

    public BlockPosition(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPosition(Location l) {
        this(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public BlockPosition(Block block) {
        this(block.getLocation());
    }

    public BlockPosition(Entity entity) {
        this(entity.getLocation());
    }

    public BlockPosition(EntityPosition pos) {
        this(pos.getWorldName(), (int)pos.getX(), (int)pos.getY(), (int)pos.getZ());
    }

    public Block getBlock() {
        return this.toLocation().getBlock();
    }

    @Override
    public Location toLocation() {
        return new Location(this.getWorld(), this.x, this.y, this.z);
    }

    @Override
    public String getWorldName() {
        return this.world;
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(this.world);
    }

    @Override
    public void setWorld(String world) {
        Preconditions.checkNotNull(world);
        this.world = world;
    }

    @Override
    public void setWorld(World world) {
        Preconditions.checkNotNull(world);
        this.world = world.getName();
    }

    @Override
    public void setX(double x) {
        this.x = Position.locToBlock(x);
    }

    @Override
    public void setY(double y) {
        this.y = Position.locToBlock(y);
    }

    @Override
    public void setZ(double z) {
        this.z = Position.locToBlock(z);
    }

    @Override
    public double distanceTo(Position pos) {
        return Math.sqrt(Math.pow((double)this.x - pos.getX(), 2.0) + Math.pow((double)this.y - pos.getY(), 2.0) + Math.pow((double)this.z - pos.getZ(), 2.0));
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public int getBlockX() {
        return this.x;
    }

    @Override
    public int getBlockY() {
        return this.y;
    }

    @Override
    public int getBlockZ() {
        return this.z;
    }

    @Override
    public Block toBlock() {
        return this.getBlock();
    }

    public BlockPosition clone() {
        return new BlockPosition(this.world, this.x, this.y, this.z);
    }

    public String toString() {
        return "BlockPosition{" + this.world + ", " + this.x + ", " + this.y + ", " + this.z + "}";
    }

    public int hashCode() {
        return Objects.hash(this.world, this.x, this.y, this.z);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        BlockPosition bp = (BlockPosition)obj;
        return this.world.equals(bp.world) && bp.x == this.x && bp.y == this.y && bp.z == this.z;
    }
}

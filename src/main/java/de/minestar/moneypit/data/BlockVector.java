package de.minestar.moneypit.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

public class BlockVector implements Comparable<BlockVector> {
    private final int x, y, z;
    private String worldName;
    private int hashCode = Integer.MIN_VALUE;
    private Location location = null;

    /**
     * Constructor
     * 
     * @param the
     *            x
     * @param the
     *            y
     * @param the
     *            z
     */
    public BlockVector(String worldName, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    /**
     * Constructor
     * 
     * @param the
     *            location
     */
    public BlockVector(Location location) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.location = location;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        if (this.location == null) {
            World world = Bukkit.getWorld(this.worldName);
            if (world != null) {
                this.location = new Location(world, this.x, this.y, this.z);
            }
        }
        return this.location;
    }

    /**
     * @return the location relative to this one
     */
    public Location getRelativeLocation(BlockFace face) {
        Location result = this.getLocation();
        if (result != null) {
            result = result.getBlock().getRelative(face).getLocation();
        }
        return result;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the z
     */
    public int getZ() {
        return z;
    }

    /**
     * @return the worldName
     */
    public String getWorldName() {
        return worldName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof BlockVector) {
            return this.equals((BlockVector) obj);
        }

        return false;
    }

    /**
     * Returns a new BlockVector that is relative to this BlockVector with the
     * given positions
     * 
     * @param x
     * @param y
     * @param z
     * @return the relative BlockVector
     */
    public BlockVector getRelative(int x, int y, int z) {
        return new BlockVector(this.worldName, this.x + x, this.y + y, this.z + z);
    }

    /**
     * Check if another BlockVector equals this BlockVector
     * 
     * @param other
     * @return <b>true</b> if the vectors are equal, otherwise <b>false</b>
     */
    public boolean equals(BlockVector other) {
        return (this.x == other.x && this.y == other.y && this.z == other.z && this.worldName.equalsIgnoreCase(other.worldName));
    }

    @Override
    public int hashCode() {
        if (hashCode == Integer.MIN_VALUE) {
            this.hashCode = this.toString().hashCode();
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return "BlockVector={ " + this.worldName + " ; " + this.x + " ; " + this.y + " ; " + this.z + " }";
    }

    @Override
    public int compareTo(BlockVector other) {
        return this.hashCode() - other.hashCode();
    }
}

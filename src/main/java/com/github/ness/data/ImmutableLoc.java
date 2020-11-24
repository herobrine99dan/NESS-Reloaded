package com.github.ness.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;

import com.github.ness.utility.MathUtils;

import lombok.Getter;

public class ImmutableLoc {

    @Getter
    private final String world;
    @Getter
    private final double x;
    @Getter
    private final double y;
    @Getter
    private final double z;
    @Getter
    private final float yaw;
    @Getter
    private final double pitch;
    @Getter
    private ImmutableVector directionVector;

    public ImmutableLoc(String world, double x, double y, double z, float yaw, double pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        makeDirection();
    }

    /**
     * Creates an immutable location from a bukkit location
     *
     * @param location the bukkit location
     * @return the immutable location
     */
    public static ImmutableLoc of(Location location) {
        return new ImmutableLoc(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    /**
     * Creates an immutable location from a bukkit location, with an overridden
     * world
     *
     * @param location  the bukkit location
     * @param worldName the world
     * @return the immutable location
     */
    public static ImmutableLoc of(Location location, String world) {
        return new ImmutableLoc(world, location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch());
    }

    private void makeDirection() {
        double rotX = yaw;
        double rotY = pitch;
        double y = -MathUtils.sin(Math.toRadians(rotY));
        double xz = MathUtils.cos(Math.toRadians(rotY));
        double x = -xz * MathUtils.sin(Math.toRadians(rotX));
        double z = xz * MathUtils.cos(Math.toRadians(rotX));
        ImmutableVector vector = new ImmutableVector(x, y, z);
        this.directionVector = vector;
    }

    public ImmutableLoc subtract(ImmutableLoc loc) {
        if (!loc.world.equals(this.world)) {
            throw new IllegalStateException("Cannot subtract from two different worlds!");
        }
        double x = 0;
        double y = 0;
        double z = 0;
        float yaw = 0;
        double pitch = 0;
        return new ImmutableLoc(this.world, x, y, z, yaw, pitch);
    }

    public double distance(ImmutableLoc o) {
        return Math.sqrt(distanceSquared(o));
    }

    public double distanceSquared(ImmutableLoc o) {
        if (o == null)
            throw new IllegalArgumentException("Cannot measure distance to a null location");
        if (o.getWorld() == null || getWorld() == null)
            throw new IllegalArgumentException("Cannot measure distance to a null world");
        return NumberConversions.square(this.x - o.x) + NumberConversions.square(this.y - o.y)
                + NumberConversions.square(this.z - o.z);
    }

    /**
     * Converts back to a bukkit location
     *
     * @return the bukkit location
     */
    public Location toBukkitLocation() {
        String worldName = getWorld();
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalStateException("World " + worldName + " vanished");
        }
        return new Location(world, getX(), getY(), getZ(), getYaw(), (float) getPitch());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((world == null) ? 0 : world.hashCode());
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImmutableLoc other = (ImmutableLoc) obj;
        if (world == null) {
            if (other.world != null)
                return false;
        } else if (!world.equals(other.world))
            return false;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z);
    }

    @Override
    public String toString() {
        return "ImmutableLoc [x=" + x + ", y=" + y + ", z=" + z + ", world=" + world + "]";
    }

}

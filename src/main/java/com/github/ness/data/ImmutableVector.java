package com.github.ness.data;

import org.bukkit.util.Vector;

public class ImmutableVector {

    private final double x;
    private final double y;
    private final double z;

    public ImmutableVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	/**
     * Creates an immutable vector from a bukkit vector, with an overridden
     * world
     *
     * @param location the bukkit vector
     * @return the immutable vector
     */
    public static ImmutableVector of(Vector vector) {
        return new ImmutableVector(vector.getX(), vector.getY(), vector.getZ());
    }

    public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	/**
     * Converts back to a bukkit location
     *
     * @return the bukkit location
     */
    public Vector toBukkitVector() {
        return new Vector(getX(), getY(), getZ());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        ImmutableVector other = (ImmutableVector) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z);
    }

    @Override
    public String toString() {
        return "ImmutableVector [x=" + x + ", y=" + y + ", z=" + z + "]";
    }
}

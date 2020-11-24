package com.github.ness.data;

import lombok.Getter;
import lombok.Setter;

public class MutableVector implements Cloneable {

    @Getter
    @Setter
    private double x;
    @Getter
    @Setter
    private double y;
    @Getter
    @Setter
    private double z;

    public MutableVector() {
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
    }

    public MutableVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MutableVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static double getEpsilon() {
        return 1.0E-6D;
    }

    public static MutableVector getMinimum(MutableVector v1, MutableVector v2) {
        return new MutableVector(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y), Math.min(v1.z, v2.z));
    }

    public static MutableVector getMaximum(MutableVector v1, MutableVector v2) {
        return new MutableVector(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z));
    }

    public MutableVector add(MutableVector vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public MutableVector subtract(MutableVector vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    public MutableVector multiply(MutableVector vec) {
        this.x *= vec.x;
        this.y *= vec.y;
        this.z *= vec.z;
        return this;
    }

    public MutableVector divide(MutableVector vec) {
        this.x /= vec.x;
        this.y /= vec.y;
        this.z /= vec.z;
        return this;
    }

    public double length() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
    }

    public float angle(MutableVector other) {
        double dot = dot(other) / length() * other.length();
        return (float) Math.acos(dot);
    }

    public MutableVector multiply(double m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    public double dot(MutableVector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public MutableVector crossProduct(MutableVector o) {
        double newX = this.y * o.z - o.y * this.z;
        double newY = this.z * o.x - o.z * this.x;
        double newZ = this.x * o.y - o.x * this.y;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        return this;
    }

    public MutableVector getCrossProduct(MutableVector o) {
        double x = this.y * o.z - o.y * this.z;
        double y = this.z * o.x - o.z * this.x;
        double z = this.x * o.y - o.x * this.y;
        return new MutableVector(x, y, z);
    }

    public MutableVector normalize() {
        double length = length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

    public boolean isInAABB(MutableVector min, MutableVector max) {
        return (this.x >= min.x && this.x <= max.x && this.y >= min.y && this.y <= max.y && this.z >= min.z
                && this.z <= max.z);
    }

    public boolean isInSphere(MutableVector origin, double radius) {
        return (square(origin.x - this.x) + square(origin.y - this.y) + square(origin.z - this.z) <= square(radius));
    }

    private double square(double x) {
        return x * x;
    }

    public int getBlockX() {
        return (int) Math.floor(this.x);
    }

    public int getBlockY() {
        return (int) Math.floor(this.y);
    }

    public int getBlockZ() {
        return (int) Math.floor(this.z);
    }

    public MutableVector clone() {
        try {
            return (MutableVector) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    @Override
    public String toString() {
        return "MutableVector [x=" + x + ", y=" + y + ", z=" + z + "]";
    }

    public ImmutableLoc toLocation(String world) {
        return new ImmutableLoc(world, this.x, this.y, this.z, 0, 0);
    }

    public ImmutableLoc toLocation(String world, float yaw, float pitch) {
        return new ImmutableLoc(world, this.x, this.y, this.z, yaw, pitch);
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
        MutableVector other = (MutableVector) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z);
    }
}

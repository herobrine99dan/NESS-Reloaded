package com.github.ness.data;

import com.github.ness.utility.Utility;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class MovementValues {

    /**
     * Yaw Difference (to.getYaw() - from.getYaw());
     */
    public final double yawDiff;
    /**
     * Pitch Difference (to.getPitch() - from.getPitch());
     */
    public final double pitchDiff;
    /**
     * X Difference (to.getX() - from.getX());
     */
    public final double xDiff;
    /**
     * Y Difference (to.getY() - from.getY());
     */
    public final double yDiff;
    /**
     * Z Difference (to.getZ() - from.getZ());
     */
    public final double zDiff;
    /**
     * XZ Difference Math.abs(xDiff) + Math.abs(zDiff);
     */
    public final double XZDiff;
    public final boolean AroundIce;
    /**
     * Liquids= Lava and Water
     */
    public final boolean AroundLiquids;

    public final boolean AroundStairs;
    /**
     * Utility.specificBlockNear(to, "slime"); or Utility.hasBlock(p, "slime");
     */
    public final boolean AroundSlime;
    public final boolean AroundSlabs;
    public final boolean AroundSnow;
    public final boolean AroundLadders;
    public final boolean AroundLily;
    public final boolean AroundCarpet;
    @Getter
    ImmutableLoc to;
    @Getter
    ImmutableLoc from;

    public MovementValues(Player p, ImmutableLoc to, ImmutableLoc from) {
        if (Bukkit.isPrimaryThread()) {
            AroundIce = Utility.specificBlockNear(to.toBukkitLocation(), "ice");
            AroundLiquids = Utility.specificBlockNear(to.toBukkitLocation(), "liquid");
            boolean slimenear = Utility.specificBlockNear(to.toBukkitLocation(), "slime");
            if (!slimenear) {
                AroundSlime = Utility.hasBlock(p, "slime");
            } else {
                AroundSlime = Utility.specificBlockNear(to.toBukkitLocation(), "slime");
            }
            AroundStairs = Utility.specificBlockNear(to.toBukkitLocation(), "stair");
            AroundSlabs = Utility.specificBlockNear(to.toBukkitLocation(), "slab");
            boolean ladders = Utility.specificBlockNear(to.toBukkitLocation(), "ladder");
            if(!ladders) {
            	ladders = Utility.specificBlockNear(to.toBukkitLocation(), "vine");
            }
            AroundLadders = ladders;
            AroundSnow = Utility.specificBlockNear(to.toBukkitLocation(), "snow");
            AroundLily = Utility.specificBlockNear(to.toBukkitLocation(), "lily");
            AroundCarpet = Utility.specificBlockNear(to.toBukkitLocation(), "carpet");
        } else {
            AroundIce = false;
            AroundSlabs = false;
            AroundCarpet = false;
            AroundLadders = false;
            AroundSnow = false;
            AroundLily = false;
            AroundLiquids = false;
            AroundSlime = false;
            AroundStairs = false;
        }
        yawDiff = to.getYaw() - from.getYaw();
        pitchDiff = to.getPitch() - from.getPitch();
        xDiff = to.getX() - from.getX();
        yDiff = to.getY() - from.getY();
        zDiff = to.getZ() - from.getZ();
        XZDiff = Math.abs(xDiff) + Math.abs(zDiff);
        this.to = to;
        this.from = from;
    }

    public ImmutableVector getDirection() {
        return this.getTo().getDirectionVector();
    }

    public CompletableFuture<Double> calculateAsyncWithCancellation(ImmutableLoc to, ImmutableLoc from)
            throws InterruptedException {
        CompletableFuture<Double> completableFuture = new CompletableFuture<Double>();
        Executors.newCachedThreadPool().submit(() -> {
            double value = to.getX() - from.getX();
            completableFuture.complete(value);
            return null;
        });

        return completableFuture;
    }

}

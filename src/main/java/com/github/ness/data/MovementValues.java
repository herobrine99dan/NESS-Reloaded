package com.github.ness.data;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.ness.utility.Utility;

import lombok.Getter;

//Need a refactor
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
    @Getter
    private final double XZDiff;
    @Getter
    private final boolean AroundIce;
    /**
     * Liquids= Lava and Water
     */
    @Getter
    private final boolean AroundLiquids;
    @Getter
    private final boolean AroundStairs;
    /**
     * Utility.specificBlockNear(to, "slime"); or Utility.hasBlock(p, "slime");
     */
    @Getter
    private final boolean AroundSlime;
    @Getter
    private final boolean AroundSlabs;
    @Getter
    private final boolean AroundSnow;
    @Getter
    private final boolean AroundLadders;
    @Getter
    private final boolean AroundLily;
    @Getter
    private final boolean AroundCarpet;
    @Getter
    private final boolean groundAround;
    @Getter
    private final boolean AroundWeb;
    @Getter
    private final ImmutableVector serverVelocity;
    @Getter
    private final boolean insideVehicle;
    @Getter
    ImmutableLoc to;
    @Getter
    ImmutableLoc from;
    @Getter
    private final GameMode gamemode;
    @Getter
    private final boolean isFlying;
    @Getter
    private final boolean ableFly;
    @Getter
    private final boolean sprinting;
    private final boolean blockUnderHead;

    public MovementValues(Player p, ImmutableLoc to, ImmutableLoc from) {
        if (Bukkit.isPrimaryThread()) {
            boolean liquids = false;
            boolean ice = false;
            boolean slime = false;
            boolean stairs = false;
            boolean slab = false;
            boolean ladder = false;
            boolean snow = false;
            boolean lily = false;
            boolean carpet = false;
            boolean ground = false;
            boolean web = false;
            serverVelocity = new ImmutableVector(p.getVelocity().getX(), p.getVelocity().getY(),
                    p.getVelocity().getZ());
            gamemode = p.getGameMode();
            isFlying = p.isFlying();
            ableFly = p.getAllowFlight();
            for (Block b : Utility.getBlocksAround(to.toBukkitLocation(), 2)) {
                String name = b.getType().name();
                if (b.isLiquid()) {
                    liquids = true;
                }
                if (b.getType().isSolid()) { // Doing all things in a single for loop is better than 12 loops
                    ground = true;
                }
                if (name.contains("ICE")) {
                    ice = true;
                } else if (name.contains("SLIME")) {
                    slime = true;
                } else if (name.contains("STAIR")) {
                    stairs = true;
                } else if (name.contains("SLAB")) {
                    slab = true;
                } else if (name.contains("LADDER")) {
                    ladder = true;
                } else if (name.contains("VINE")) {
                    ladder = true;
                } else if (name.contains("SNOW")) {
                    snow = true;
                } else if (name.contains("LILY")) {
                    lily = true;
                } else if (name.contains("CARPET")) {
                    carpet = true;
                } else if (name.contains("WEB")) {
                    web = true;
                }
            }
            AroundSnow = snow;
            blockUnderHead = Utility.groundAround(to.toBukkitLocation().add(0, 1.8, 0));
            AroundLadders = ladder;
            AroundSlabs = slab;
            AroundWeb = web;
            AroundStairs = stairs;
            sprinting = p.isSprinting();
            if (!slime) {
                slime = Utility.hasBlock(p, "SLIME");
            }
            AroundSlime = slime;
            AroundIce = ice;
            AroundLily = lily;
            groundAround = ground;
            insideVehicle = p.isInsideVehicle();
            AroundCarpet = carpet;
            AroundLiquids = liquids;
        } else {
            AroundIce = false;
            AroundSlabs = false;
            serverVelocity = new ImmutableVector(0, 0, 0);
            AroundCarpet = false;
            AroundLadders = false;
            groundAround = false;
            sprinting = false;
            AroundLily = false;
            AroundWeb = false;
            blockUnderHead = false;
            AroundSnow = false;
            insideVehicle = false;
            gamemode = GameMode.SURVIVAL;
            isFlying = false;
            ableFly = false;
            AroundLiquids = false;
            AroundSlime = false;
            AroundStairs = false;
        }
        yawDiff = to.getYaw() - from.getYaw();
        pitchDiff = to.getPitch() - from.getPitch();
        xDiff = to.getX() - from.getX();
        yDiff = to.getY() - from.getY();
        zDiff = to.getZ() - from.getZ();
        XZDiff = Math.hypot(xDiff, zDiff);
        this.to = to;
        this.from = from;
    }

    public boolean hasBlockNearHead() {
        return blockUnderHead;
    }

    /**
     * Check if the player is falling using his server Motion
     * 
     * @return true if the player is falling
     */
    public boolean isServerFalling() {
        return this.serverVelocity.getY() < 0;
    }

    /**
     * Check if the player is falling using his yDifference
     * 
     * @return true if the player is falling
     */
    public boolean isClientFalling() {
        return this.yDiff < 0;
    }

    public void doCalculations() {

    }

    public ImmutableVector getDirection() {
        return this.getTo().getDirectionVector();
    }
}

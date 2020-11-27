package com.github.ness.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.blockgetter.MaterialAccess;
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
    private final boolean AroundChest;
    @Getter
    private final boolean AroundPot;
    @Getter
    private final boolean AroundBed;
    @Getter
    private final boolean AroundDetector;
    @Getter
    private final boolean AroundChorus;
    @Getter
    private final boolean AroundScaffolding;
    @Getter
    private final ImmutableVector serverVelocity;
    @Getter
    private final boolean insideVehicle;
    @Getter
    private ImmutableLoc to;
    @Getter
    private ImmutableLoc from;
    @Getter
    private final GameMode gamemode;
    @Getter
    private final boolean isFlying;
    @Getter
    private final boolean ableFly;
    @Getter
    private final boolean sprinting;
    private final boolean blockUnderHead;
    @Getter
    private final String vehicle;
    @Getter
    private final float fallDistance;
    @Getter
    private final boolean flyBypass;
    @Getter
    private final boolean thereVehicleNear;
    @Getter
    private final boolean sneaking;
    @Getter
    private final boolean dead;
    @Getter
    private final boolean entityAround;
    @Getter
    private final boolean AroundSea;
    @Getter
    private final boolean bliendnessEffect;
    @Getter
    private final int speedPotion;
    @Getter
    private final int jumpPotion;
    @Getter
    private final int foodLevel;
    @Getter
    private final boolean trapdoorNear;
    @Getter
    private final float walkSpeed;
    @Getter
    private final ImmutableBlock toBlock;
    @Getter
    private final ImmutableBlock fromBlock;
    @Getter
    private final ImmutableBlock blockUnder;
    @Getter
    private final List<ImmutableBlock> blocks;

    // TODO For now ImmutableLoc objects are updated by PlayerMove, make FlyingEvent
    // update them
    public MovementValues(Player p, ImmutableLoc to, ImmutableLoc from, MaterialAccess materialAccess) {
        blocks = new ArrayList<ImmutableBlock>();
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
            boolean sea = false;
            boolean trapdoor = false;
            boolean bed = false;
            boolean pot = false;
            boolean chest = false;
            boolean detector = false;
            boolean chorus = false;
            boolean scaffolding = false;
            serverVelocity = new ImmutableVector(p.getVelocity().getX(), p.getVelocity().getY(),
                    p.getVelocity().getZ());
            gamemode = p.getGameMode();
            isFlying = p.isFlying();
            ableFly = p.getAllowFlight();
            for (Block b : Utility.getBlocksAround(to.toBukkitLocation(), 2)) {
                blocks.add(ImmutableBlock.of(b));
                String name = materialAccess.getMaterial(b).name();
                if (name.contains("WATER") || name.contains("LAVA") || name.contains("LIQUID")) {
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
                } else if (name.contains("SEA")) {
                    sea = true;
                } else if (name.contains("TRAP")) {
                    trapdoor = true;
                } else if (name.contains("BED")) {
                    bed = true;
                } else if (name.contains("POT")) {
                    pot = true;
                } else if (name.contains("CHEST")) {
                    chest = true;
                } else if (name.contains("DETECTOR")) {
                    detector = true;
                } else if (name.contains("CHORUS")) {
                    chorus = true;
                } else if (name.contains("SCAFFOLDING")) {
                    scaffolding = true;
                }
            }
            AroundSea = sea;
            AroundScaffolding = scaffolding;
            AroundPot = pot;
            foodLevel = p.getFoodLevel();
            AroundChest = chest;
            AroundDetector = detector;
            AroundBed = bed;
            AroundSnow = snow;
            AroundChorus = chorus;
            trapdoorNear = trapdoor;
            blockUnderHead = Utility.groundAround(to.toBukkitLocation().add(0, 1.8, 0));
            AroundLadders = ladder;
            AroundSlabs = slab;
            toBlock = ImmutableBlock.of(to.toBukkitLocation().getBlock());
            fromBlock = ImmutableBlock.of(from.toBukkitLocation().getBlock());
            AroundWeb = web;
            walkSpeed = p.getWalkSpeed();
            AroundStairs = stairs;
            sprinting = p.isSprinting();
            dead = p.isDead();
            entityAround = p.getNearbyEntities(2, 2, 2).isEmpty();
            sneaking = p.isSneaking();
            thereVehicleNear = Utility.hasVehicleNear(p, 4);
            if (!slime) {
                slime = Utility.hasBlock(p, "SLIME");
            }
            AroundSlime = slime;
            AroundIce = ice;
            blockUnder = ImmutableBlock.of(to.toBukkitLocation().clone().add(0, -0.5, 0).getBlock());
            speedPotion = Utility.getPotionEffectLevel(p, PotionEffectType.SPEED);
            jumpPotion = Utility.getPotionEffectLevel(p, PotionEffectType.JUMP);
            bliendnessEffect = p.hasPotionEffect(PotionEffectType.BLINDNESS);
            AroundLily = lily;
            groundAround = ground;
            insideVehicle = p.isInsideVehicle();
            AroundCarpet = carpet;
            AroundLiquids = liquids;
            fallDistance = p.getFallDistance();
            flyBypass = Utility.hasflybypass(p);
            if (p.getVehicle() != null) {
                this.vehicle = p.getVehicle().getType().name();
            } else {
                this.vehicle = "";
            }
        } else {
            AroundIce = false;
            trapdoorNear = false;
            AroundSlabs = false;
            serverVelocity = new ImmutableVector(0, 0, 0);
            AroundCarpet = false;
            AroundLadders = false;
            blockUnder = new ImmutableBlock((int) to.getX(), (int) (to.getY() - 0.5), (int) to.getZ(), "STONE",
                    true, true);
            toBlock = new ImmutableBlock((int) to.getX(), (int) to.getY(), (int) to.getZ(), "STONE", true, true);
            fromBlock = new ImmutableBlock((int) from.getX(), (int) from.getY(), (int) from.getZ(), "STONE", true,
                    true);
            walkSpeed = 0.2f;
            foodLevel = 20;
            groundAround = false;
            speedPotion = 0;
            jumpPotion = 0;
            AroundPot = false;
            AroundChest = false;
            AroundDetector = false;
            AroundBed = false;
            AroundChorus = false;
            bliendnessEffect = false;
            AroundSea = false;
            sprinting = false;
            AroundLily = false;
            thereVehicleNear = false;
            AroundWeb = false;
            blockUnderHead = false;
            AroundSnow = false;
            insideVehicle = false;
            dead = false;
            AroundScaffolding = false;
            entityAround = false;
            gamemode = GameMode.SURVIVAL;
            isFlying = false;
            fallDistance = 0f;
            ableFly = false;
            flyBypass = false;
            sneaking = false;
            AroundLiquids = false;
            AroundSlime = false;
            AroundStairs = false;
            this.vehicle = "";
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

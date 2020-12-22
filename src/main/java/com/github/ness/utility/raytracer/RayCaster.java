package com.github.ness.utility.raytracer;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.ness.utility.Utility;

public class RayCaster {

    private Location origin;
    private double maxDistance;
    private Player player;
    private Block blockFounded;
    private Entity entityFounded;

    public RayCaster(Player player, Location origin, double maxDistance) {
        this.origin = origin;
        this.player = player;
        this.maxDistance = maxDistance;
    }

    public void compute(RayCaster.RaycastType type) {
        if (type == RayCaster.RaycastType.ENTITY) {
            for (Entity e : this.origin.getWorld().getNearbyEntities(origin, maxDistance, maxDistance, maxDistance)) {
                if (e instanceof LivingEntity) {
                    if(player.hasLineOfSight(e)) {
                        entityFounded = e;
                    }
                }
            }
        } else if (type == RayCaster.RaycastType.BLOCK) {
            Location loc = player.getEyeLocation();
            Vector v = loc.getDirection().normalize();
            for (double i = 1; i <= maxDistance; i += 0.1) {
                loc.add(v);
                if (loc.getBlock().getType().isOccluding()) {
                    blockFounded = loc.getBlock();
                }
            }
        }
    }

    public static enum RaycastType {
        ENTITY, BLOCK;
    }

}

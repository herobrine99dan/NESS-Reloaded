package com.github.ness.utility.raytracer;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.ness.utility.raytracer.rays.Ray;

public class RayCaster {

	private double maxDistance;
	private Player player;
	private Block blockFounded;
	private Entity entityFounded;

	public RayCaster(Player player, double maxDistance) {
		this.player = player;
		this.maxDistance = maxDistance;
	}

	public void compute(RayCaster.RaycastType type) {
		if (type == RayCaster.RaycastType.ENTITY) {
			for (Entity e : this.player.getWorld().getNearbyEntities(this.player.getLocation(), maxDistance,
					maxDistance, maxDistance)) {
				if (e instanceof LivingEntity) {
					if (player.hasLineOfSight(e) && !e.equals(player)) {
						entityFounded = e;
					}
				}
			}
		} else if (type == RayCaster.RaycastType.BLOCK) {
			Ray ray = Ray.from(player);
			for (double x = 0; x < maxDistance; x += 0.3) {
				Location vector = ray.getPoint(x).toLocation(player.getWorld());
				if(!vector.getBlock().getType().name().contains("AIR")) {
					blockFounded = vector.getBlock();
					break;
				}
			}
		}
	}

	public Block getBlockFounded() {
		return blockFounded;
	}

	public Entity getEntityFounded() {
		return entityFounded;
	}

	public static enum RaycastType {
		ENTITY, BLOCK;
	}

}

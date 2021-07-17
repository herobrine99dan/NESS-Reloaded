package com.github.ness.utility.raytracer;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.ness.NessAnticheat;
import com.github.ness.utility.raytracer.rays.AABB;
import com.github.ness.utility.raytracer.rays.Ray;

public class RayCaster {

	private double maxDistance;
	private final Player player;
	private Block blockFounded;
	private Entity entityFounded;
	private static NessAnticheat ness;
	private final RayCaster.RaycastType type;

	public RayCaster(Player player, double maxDistance, RayCaster.RaycastType type, NessAnticheat ness) {
		this.player = player;
		this.maxDistance = maxDistance;
		this.type = type;
	}

	public RayCaster compute() {
		if (type == RayCaster.RaycastType.BLOCK) {
			Ray ray = Ray.from(player);
			for (double x = 0; x < maxDistance; x += 0.3) {
				Location vector = ray.getPoint(x).toLocation(player.getWorld());
				if (!vector.getBlock().getType().name().contains("AIR")) {
					blockFounded = vector.getBlock();
					break;
				}
			}
		} else if (type == RayCaster.RaycastType.ENTITY) {
			for (Entity entity : this.player.getWorld().getNearbyEntities(this.player.getLocation(), maxDistance,
					maxDistance, maxDistance)) {
				if (entity instanceof LivingEntity) {
					final Ray ray = Ray.from(player);
					final AABB aabb = AABB.from(entity, ness, 0.25);
					double range = aabb.collidesD(ray, 0, maxDistance);
					if (range != -1) {
						this.entityFounded = entity;
					}
				}
			}
		}
		return this;
	}

	public Block getBlockFound() {
		return blockFounded;
	}

	public Entity getEntityFound() {
		return entityFounded;
	}

	public static enum RaycastType {
		ENTITY, BLOCK;
	}

}

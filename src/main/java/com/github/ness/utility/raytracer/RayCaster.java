package com.github.ness.utility.raytracer;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.ness.utility.Utility;

public class RayCaster {

	Location origin;
	double maxDistance;
	Player player;

	public RayCaster(Player player, Location origin, double maxDistance) {
		this.origin = origin;
		this.player = player;
		this.maxDistance = maxDistance;
	}

	public void compute(RayCaster.RaycastType type) {
		if (type == RayCaster.RaycastType.ENTITY) {
			for (Entity e : this.origin.getWorld().getNearbyEntities(origin, maxDistance, maxDistance, maxDistance)) {
				if (e instanceof LivingEntity) {
					Utility.getAngle(player, e.getLocation(), origin.getDirection());
				}
			}
		} else if (type == RayCaster.RaycastType.BLOCK) {
			
		}
	}

	public static enum RaycastType {
		ENTITY, BLOCK;
	}

}

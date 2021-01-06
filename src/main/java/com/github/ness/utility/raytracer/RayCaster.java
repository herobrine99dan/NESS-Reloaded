package com.github.ness.utility.raytracer;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
			blockFounded = player.getTargetBlock((Set<Material>) null, (int) maxDistance);
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

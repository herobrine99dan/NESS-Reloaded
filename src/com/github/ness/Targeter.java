package com.github.ness;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Targeter {

	protected static Entity getTarget(final Entity entity, final List<Entity> entities) {
		if (entity == null)
			return null;
		Entity target = null;
		final double threshold = 1;
		for (final Entity other : entities) {
			final Vector n = other.getLocation().toVector().subtract(entity.getLocation().toVector());
			if (entity.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold
					&& n.normalize().dot(entity.getLocation().getDirection().normalize()) >= 0) {
				if (target == null || target.getLocation().distanceSquared(entity.getLocation()) > other.getLocation()
						.distanceSquared(entity.getLocation()))
					target = other;
			}
		}
		return target;
	}

}
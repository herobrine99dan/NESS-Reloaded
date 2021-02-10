package com.github.ness.utility.raytracer.rays;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.ness.NessAnticheat;
import com.github.ness.reflect.ClassLocator;
import com.github.ness.reflect.FieldInvoker;
import com.github.ness.reflect.MemberDescriptions;
import com.github.ness.reflect.MethodInvoker;
import com.github.ness.reflect.SimpleClassLocator;

public class AABB {

	/**
	 * From https://www.spigotmc.org/threads/hitboxes-and-ray-tracing.174358/
	 * 
	 * @author 567legodude
	 */

	private final Vector min, max; // min/max locations
	private static MethodInvoker<?> craftLivingEntityMethod;
	private static MethodInvoker<?> getBoundingBoxesMethod;
	private static FieldInvoker<Double> xMinField, xMaxField, yMinField, yMaxField, zMinField, zMaxField;
	private static ClassLocator locator = SimpleClassLocator.create();

	// Create Bounding Box from min/max locations.
	public AABB(Vector min, Vector max) {
		this(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}

	// Main constructor for AABB
	public AABB(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.min = new Vector(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
		this.max = new Vector(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
	}

	private AABB(Entity entity, NessAnticheat ness, double expansion) {
		Vector min = new Vector(0, 0, 0);
		Vector max = new Vector(0, 0, 0);
		if (entity instanceof Player) {
			Player player = (Player) entity;
			min = getMinForPlayer(player.getLocation(), player).add(new Vector(-expansion, -expansion, -expansion));
			max = getMaxForPlayer(player.getLocation(), player).add(new Vector(expansion, expansion, expansion));
		} else if (ness != null) {
			double xMax, xMin, yMax, yMin, zMax, zMin = 0;
			if (craftLivingEntityMethod == null) {
				craftLivingEntityMethod = ness.getReflectHelper().getMethod(
						locator.getObcClass("entity.CraftLivingEntity"), MemberDescriptions.forMethod("getHandle"));
			}
			if (getBoundingBoxesMethod == null) {
				getBoundingBoxesMethod = ness.getReflectHelper().getMethod(locator.getNmsClass("Entity"),
						MemberDescriptions.forMethod("getBoundingBox"));
			}
			if (xMaxField == null) {
				// Max fields are "d" "e" and "f"
				// Min fields are "a" "b" and "c"
				Class<?> boxClass = locator.getNmsClass("AxisAlignedBB");
				xMaxField = ness.getReflectHelper().getField(boxClass, MemberDescriptions.forField(double.class, "d"));
				yMaxField = ness.getReflectHelper().getField(boxClass, MemberDescriptions.forField(double.class, "e"));
				zMaxField = ness.getReflectHelper().getField(boxClass, MemberDescriptions.forField(double.class, "f"));
				xMinField = ness.getReflectHelper().getField(boxClass, MemberDescriptions.forField(double.class, "a"));
				yMinField = ness.getReflectHelper().getField(boxClass, MemberDescriptions.forField(double.class, "b"));
				zMinField = ness.getReflectHelper().getField(boxClass, MemberDescriptions.forField(double.class, "c"));
			}
			Object entitynms = craftLivingEntityMethod.invoke(entity);
			Object boundingBox = getBoundingBoxesMethod.invoke(entitynms);
			xMin = xMinField.get(boundingBox);
			yMin = yMinField.get(boundingBox);
			zMin = zMinField.get(boundingBox);
			xMax = xMaxField.get(boundingBox);
			yMax = yMaxField.get(boundingBox);
			zMax = zMaxField.get(boundingBox);
			min = new Vector(xMin - expansion, yMin - expansion, zMin - expansion);
			max = new Vector(xMax + expansion, yMax + expansion, zMax + expansion);
		}
		this.max = max;
		this.min = min;
	}

	private Vector getMinForPlayer(Location loc, Player player) {
		return loc.toVector().add(new Vector(-0.3, 0, -0.3));
	}

	private Vector getMaxForPlayer(Location loc, Player player) {
		return loc.toVector().add(new Vector(0.3, player.getEyeHeight(), 0.3));
	}

	public static AABB from(Entity player, NessAnticheat ness, double expansion) {
		return new AABB(player, ness, expansion);
	}

	public Vector getMin() {
		return min;
	}

	public Vector getMax() {
		return max;
	}

	// Returns minimum x, y, or z point from inputs 0, 1, or 2.
	public double min(int i) {
		switch (i) {
		case 0:
			return min.getX();
		case 1:
			return min.getY();
		case 2:
			return min.getZ();
		default:
			return 0;
		}
	}

	// Returns maximum x, y, or z point from inputs 0, 1, or 2.
	public double max(int i) {
		switch (i) {
		case 0:
			return max.getX();
		case 1:
			return max.getY();
		case 2:
			return max.getZ();
		default:
			return 0;
		}
	}

	// Check if a Ray passes through this box. tmin and tmax are the bounds.
	// Example: If you wanted to see if the Ray collides anywhere from its
	// origin to 5 units away, the values would be 0 and 5.
	public boolean collides(Ray ray, double tmin, double tmax) {
		for (int i = 0; i < 3; i++) {
			double d = 1 / ray.direction(i);
			double t0 = (min(i) - ray.origin(i)) * d;
			double t1 = (max(i) - ray.origin(i)) * d;
			if (d < 0) {
				double t = t0;
				t0 = t1;
				t1 = t;
			}
			tmin = t0 > tmin ? t0 : tmin;
			tmax = t1 < tmax ? t1 : tmax;
			if (tmax <= tmin)
				return false;
		}
		return true;
	}

	// Same as other collides method, but returns the distance of the nearest
	// point of collision of the ray and box, or -1 if no collision.
	public double collidesD(Ray ray, double tmin, double tmax) {
		for (int i = 0; i < 3; i++) {
			double d = 1 / ray.direction(i);
			double t0 = (min(i) - ray.origin(i)) * d;
			double t1 = (max(i) - ray.origin(i)) * d;
			if (d < 0) {
				double t = t0;
				t0 = t1;
				t1 = t;
			}
			tmin = t0 > tmin ? t0 : tmin;
			tmax = t1 < tmax ? t1 : tmax;
			if (tmax <= tmin)
				return -1;
		}
		return tmin;
	}

	// Check if the location is in this box.
	public boolean contains(Location location) {
		if (location.getX() > max.getX())
			return false;
		if (location.getY() > max.getY())
			return false;
		if (location.getZ() > max.getZ())
			return false;
		if (location.getX() < min.getX())
			return false;
		if (location.getY() < min.getY())
			return false;
		if (location.getZ() < min.getZ())
			return false;
		return true;
	}
}
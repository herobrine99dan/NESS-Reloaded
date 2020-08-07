package com.github.ness;

import lombok.Getter;

public class ImmutableLoc {

	@Getter
	private final double x;
	@Getter
	private final double y;
	@Getter
	private final double z;
	@Getter
	private final String world;

	public ImmutableLoc(String world, double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImmutableLoc)) {
			return false;
		} else {
			ImmutableLoc loc = (ImmutableLoc) obj;
			return loc.getZ() == this.getZ() && loc.getX() == this.getX() && loc.getY() == this.getY()
					&& loc.getWorld().equals(this.getWorld());
		}
	}

	@Override
	public String toString() {
		return "World: " + this.getWorld() + ",X: " + this.getX() + ",Y: " + this.getY() + ",Z: " + this.getZ();
	}

}

package com.github.ness.check;

public enum ChecksPackage {

	COMBAT("combat"),
	MOVEMENT("movement"),
	FLYCHEAT("movement.fly"),
	PACKET("packet"),
	WORLD("world"),
	MISC("misc");

	/**
	 * Class names of checks in the required checks package. <br>
	 * Do not mutate the array
	 */
	static final String[] REQUIRED_CHECKS = { "TeleportEvent" };

	private final String prefix;

	ChecksPackage(String prefix) {
		this.prefix = prefix;
	}

	public String prefix() {
		return prefix;
	}

}

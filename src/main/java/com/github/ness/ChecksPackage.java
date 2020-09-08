package com.github.ness;

enum ChecksPackage {

	MAIN(""),
	COMBAT(".combat"),
	MOVEMENT(".movement"),
	FLYCHEAT(".movement.fly"),
	PACKET(".packet"),
	WORLD(".world"),
	MISC(".misc");
	
	/**
	 * Class names of checks in the required checks package. <br>
	 * Do not mutate the array
	 */
	static final String[] REQUIRED_CHECKS = {"TeleportEvent"};
	
	private final String prefix;
	
	ChecksPackage(String prefix) {
		this.prefix = prefix;
	}
	
	String prefix() {
		return prefix;
	}
	
}

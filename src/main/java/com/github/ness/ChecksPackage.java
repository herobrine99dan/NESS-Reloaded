package com.github.ness;

enum ChecksPackage {

	MAIN(""),
	COMBAT(".combat"),
	MOVEMENT(".movement"),
	PACKET(".packet"),
	WORLD(".world"),
	MISC(".misc");
	
	private final String prefix;
	
	ChecksPackage(String prefix) {
		this.prefix = prefix;
	}
	
	String prefix() {
		return prefix;
	}
	
}

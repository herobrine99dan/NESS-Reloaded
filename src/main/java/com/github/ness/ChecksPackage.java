package com.github.ness;

enum ChecksPackage {

	MAIN(""),
	COMBAT(".combat"),
	MISC(".misc");
	
	private final String prefix;
	
	ChecksPackage(String prefix) {
		this.prefix = prefix;
	}
	
	String prefix() {
		return prefix;
	}
	
}

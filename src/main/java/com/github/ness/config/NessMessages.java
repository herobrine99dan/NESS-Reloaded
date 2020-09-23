package com.github.ness.config;

import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

public interface NessMessages {

	@SubSection
	Commands commands();
	
	interface Commands {
		
		@ConfKey("not-found-player")
		@DefaultString("&cTarget player not specified, not found, or offline.")
		String notFoundPlayer();
		
		@ConfKey("show-violations.header")
		@DefaultString("&7Violations for &e%TARGET%")
		String showViolationsHeader();
		
		@ConfKey("show-violations.body")
		@DefaultString("&7Hack: &e%HACK%&7. Count: %VIOLATIONS%")
		String showViolationsBody();
		
	}
	
}

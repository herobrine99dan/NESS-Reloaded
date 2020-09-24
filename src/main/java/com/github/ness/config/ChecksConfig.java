package com.github.ness.config;

import com.github.ness.check.combat.AutoClick;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfKey;

public interface ChecksConfig {

	@ConfKey("autoclick")
	@ConfComments({
		"",
		"AutoClick",
		"Caps clicks per second (CPS) at a hard limit, also calculates",
		"the variance in the user's clicks (constancy) and the variance",
		"in the variance (constancy super).",
		"",
		"Performance impact: Minimal",
		""})
	AutoClick.CheckConf autoClick();
	
}

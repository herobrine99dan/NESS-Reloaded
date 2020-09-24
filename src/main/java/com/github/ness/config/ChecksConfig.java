package com.github.ness.config;

import com.github.ness.check.combat.AutoClick;
import com.github.ness.check.combat.AutoClicker;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

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
	@ConfKey("autoclicker")
	@ConfComments({
		"",
		"AutoClicker",
		"A Simple Max CPS Check",
		"",
		"Performance impact: Minimal", 
		"Effectiveness: Medium",
		""})
	AutoClicker.Config autoClicker();
	
}

package com.github.ness.config;

import com.github.ness.check.combat.AutoClick;
import com.github.ness.check.combat.AutoClicker;
import com.github.ness.check.combat.Killaura;
import com.github.ness.check.combat.PlayerESP;

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
	@SubSection
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
	@SubSection
	AutoClicker.Config autoClicker();
	@ConfKey("killaura")
	@ConfComments({
		"",
		"Check if a player is using ForceField / ClickAura / Killaura",
		"This Check detect only some old clients ",
		"",
		"Performance impact: low", 
		"Effectiveness: Medium",
		""})
	@SubSection
	Killaura.Config killaura();
	@ConfKey("playeresp")
	@ConfComments({
		"",
		"Hide players that a player can't see",
		"",
		"Performance impact: Medium", 
		"Effectiveness: High",
		""})
	@SubSection
	PlayerESP.Config playerESP();
	
}

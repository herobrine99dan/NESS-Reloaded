package com.github.ness.config;

import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;

import java.util.List;

import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

/**
 * Top level configuration interface for NESS
 * 
 * @author A248
 *
 */
public interface NessConfig {

	@ConfKey("dev-mode")
	@DefaultBoolean(false)
	boolean isDevMode();
	
	@ConfKey("enabled-checks")
	@DefaultStrings({"Aimbot",
		"AimbotGCD",
		"AutoClicker",
		/* "AntiUnicode", */
		"AntiKb",
		"Timer",
		"Criticals",
		"ChestStealer",
		/* "FastEat", */
		"FastLadder",
		"FastPlace",
		/* "ChestESP", */
		"FlyGhostMode",
		"FlyInvalidMove",
		"FlyHighJump",
		"FlyInvalidJumpMotion",
		"FlyInvalidGravity",
		"FlyFalseGround",
		"FlyHighDistance",
		"GhostHand",
		"LiquidInteraction",
		"InventoryHack",
		"Killaura",
		"MorePackets",
		"NoSlowBow",
		"NoSlowFood",
		"NoWeb",
		"NoGround",
		"NoGround",
		"NoFall",
		"Speed",
		"Step",
		"Phase",
		"ImpossibleBreak",
		/* "SpeedAir", */
		"ScaffoldFalseTarget",
		"ScaffoldIllegalTarget",
		"ScaffoldAngle"})
	List<String> getEnabledChecks();
	
	@ConfKey("violation-handling")
	@SubSection
	ViolationHandling getViolationHandling();
	
	@ConfKey("notify-staff")
	@SubSection
	NotifyStaff getNotifyStaff();
	
	@ConfKey("checks")
	@SubSection
	ChecksConfig getCheckSection();
	
}

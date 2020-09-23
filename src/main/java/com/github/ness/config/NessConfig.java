package com.github.ness.config;

import space.arim.dazzleconf.annote.ConfComment;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;

import java.util.List;

import com.github.ness.antibot.AntiBotConfig;
import com.github.ness.violation.ViolationHandling;

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
	@ConfComment("Enable developer mode")
	@DefaultBoolean(false)
	boolean isDevMode();
	
	@ConfKey("enabled-checks")
	@ConfComments({
		"",
		"",
		"Enabled checks",
		"",
		"Comment out a check to disable",
		""})
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
	
	@ConfKey("antibot")
	@ConfComments({
		"",
		"AntiBot",
		"",
		"Blocks Bot Attacks which sends a lot of players",
		""})
	@SubSection
	AntiBotConfig getAntiBot();
	
	@ConfKey("violation-handling")
	@ConfComments({
		"",
		"Violation handling",
		"",
		"What to do when a player is detected for cheats",
		""
	})
	@SubSection
	ViolationHandling getViolationHandling();
	
	@ConfKey("checks")
	@SubSection
	ChecksConfig getCheckSection();
	
}

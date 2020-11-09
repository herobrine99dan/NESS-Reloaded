package com.github.ness.config;

import java.util.List;

import com.github.ness.antibot.AntiBotConfig;
import com.github.ness.violation.ViolationHandling;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

/**
 * Top level configuration interface for NESS
 * 
 * @author A248
 *
 */
@ConfHeader({
	"",
	"",
	"NESS Reloaded v2 Configuration",
	"",
	"Discord: https://discord.gg/63JGnay",
	"Github: https://github.com/herobrine99dan/NESS-Reloaded",
	"",
	""})
public interface NessConfig {

	@ConfKey("dev-mode")
	@ConfComments("Enable developer mode")
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
	@DefaultStrings({"#AbilititiesSpoofed","Aimbot",
		"AimbotGCD",
		"AutoClicker",
		/* "AntiUnicode", */
		"AntiKb",
		"BlockBreakAction",
		"Timer",
		"Criticals",
		"ChestStealer",
		"EntityFly",
		/* "FastEat", */
		"FastLadder",
		"FastPlace",
		/* "ChestESP", */
		"FlyGhostMode",
		"FlyHighJump",
		"FastSneak",
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
		"NoFall",
		"Speed",
		"SpeedAir",
		"Jesus",
		"Step",
		"Phase",
		"ImpossibleBreak",
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

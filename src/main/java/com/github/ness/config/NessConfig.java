package com.github.ness.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.ness.antibot.AntiBotConfig;
import com.github.ness.violation.ViolationHandling;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
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
@ConfHeader({ "", "", "NESS Reloaded v2 Configuration", "", "Discord: https://discord.gg/63JGnay",
		"Github: https://github.com/herobrine99dan/NESS-Reloaded", "", "" })
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
		//"FastSneak",
		"FlyInvalidJumpMotion",
		"FlyInvalidServerGravity",
		"FlyInvalidClientGravity",
		"FlyFalseGround",
		"FlyHighDistance",
		"FlyEqualDistance",
		"FlyEqualYMotion",
		"VerticalFly",
		"Freecam",
		"GhostHand",
		"LiquidInteraction",
		"InventoryHack",
		"Killaura",
		"KillauraKeepSprint",
		"MorePackets",
		"NoSlowBow",
		"NoSlowFood",
		"IrregularMovement",
		"InvalidDirection",
		"Regen",
		"NoWeb",
		"NoGround",
		"NoFall",
		"Speed",
		"#SpeedAir", 
		"InvalidSprint",
		"SpeedFriction",
		"Jesus",
		"Step",
		"Phase", 
		"OmniSprint",
		"ImpossibleBreak",
		"ScaffoldFalseTarget",
		"ScaffoldDownWard",
		"ScaffoldAngle"})
	List<String> getEnabledChecks();

	@ConfKey("antibot")
	@ConfComments({ "", "AntiBot", "", "Blocks Bot Attacks which sends a lot of players", "" })
	@SubSection
	AntiBotConfig getAntiBot();

	@ConfKey("violation-handling")
	@ConfComments({ "",
			"Violation handling (Global)",
			"",
			"What to do when a player is detected for cheats",
			"",
			"The triggers enabled below will run for ALL checks.",
			"If you want to use violation handling per check, you should disable",
			"the triggers in this section, and configurate triggers per check instead."})
	@SubSection
	ViolationHandling getViolationHandling();

	@ConfKey("per-check-overrides")
	@ConfComments({
			"Additional per-check overrides, specifically violation handling.",
			"",
			"Each section is named after the check name.",
			"The AutoClicker check is given as an example"})
	@ConfDefault.DefaultObject("defaultViolationHandlingPerCheck")
	Map<String, @SubSection CheckConfig> perCheckConfiguration();

	static Map<String, CheckConfig> defaultViolationHandlingPerCheck(CheckConfig defaultCheckConf) {
		Map<String, CheckConfig> map = new HashMap<>();
		map.put("AutoClicker", defaultCheckConf);
		return map;
	}

	@ConfKey("checks")
	@SubSection
	AllChecksConfig getCheckSection();

}

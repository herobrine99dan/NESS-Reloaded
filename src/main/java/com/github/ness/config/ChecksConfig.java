package com.github.ness.config;

import com.github.ness.check.combat.AutoClicker;
import com.github.ness.check.combat.Killaura;
import com.github.ness.check.combat.PlayerESP;
import com.github.ness.check.combat.autoclick.AutoClickConfig;
import com.github.ness.check.movement.ElytraCheats;
import com.github.ness.check.movement.FastLadder;
import com.github.ness.check.movement.Jesus;
import com.github.ness.check.movement.fly.FlyInvalidServerGravity;
import com.github.ness.check.packet.Freecam;
import com.github.ness.check.packet.MorePackets;
import com.github.ness.check.packet.Timer;
import com.github.ness.check.world.FastPlace;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

@ConfHeader("All configuration relating to specific checks")
public interface ChecksConfig {

	@ConfKey("autoclick")
	@SubSection
	AutoClickConfig autoClick();
	
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
	@ConfKey("fastladder")
	@ConfComments({
		"",
		"Check if a player is climing a ladder/vine too fast", 
		"Use 0.21 to have GeyserMC Compatibility, if you have only Java Edition players use 0.155",
		"",
		"Performance impact: low", 
		"Effectiveness: High",
		""})
	@SubSection
	FastLadder.Config fastLadder();
	@ConfKey("fastplace")
	@ConfComments({
		"",
		"Check if a player place too many blocks in a seconds", 
		"",
		"Performance impact: low", 
		"Effectiveness: Medium",
		""})
	@SubSection
	FastPlace.Config fastPlace();
	@ConfKey("elytracheats")
	@ConfComments({
		"",
		"Check if a player fly too quickly with elytra", 
		"",
		"Performance impact: low", 
		"Effectiveness: High",
		""})
	@SubSection
	ElytraCheats.Config elytraCheats();
	@ConfKey("morepackets")
	@ConfComments({
		"",
		"Check if a player sends too many packets.", "This blocks some Regen, Nuker and some ServerCrasher exploits",
		"",
		"Performance impact: low", 
		"Effectiveness: High",
		""})
	@SubSection
	MorePackets.Config morePackets();
	@ConfKey("gravity-fly")
	@ConfComments({
		"",
		"Check if a player is editing gravity.",
		"",
		"Performance impact: low", 
		"Effectiveness: High",
		""})
	@SubSection
	FlyInvalidServerGravity.Config flyInvalidServerGravity();
	@ConfKey("freecam")
	@SubSection
	Freecam.Config freecam();
	@ConfKey("jesus")
	@ConfComments({
		"",
		"Check the speed of a player in Water.",
		"Use 1 to have GeyserMC Compatibility, if you have only Java Edition players use 0.75",
		"",
		"Performance impact: low", 
		"Effectiveness: Medium",
		""})
	@SubSection
	Jesus.Config jesus();
	@ConfKey("timer")
	@ConfComments({
		"",
		"Check how many packets a player sends e",
		"",
		"Performance impact: low", 
		"Effectiveness: High",
		""})
	@SubSection
	Timer.Config timer();
}

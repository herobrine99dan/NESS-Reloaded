package com.github.ness.config;

import com.github.ness.check.combat.Killaura;
import com.github.ness.check.combat.KillauraHitMissRatio;
import com.github.ness.check.combat.MaxCPS;
import com.github.ness.check.combat.PlayerESP;
import com.github.ness.check.combat.VerticalVelocity;
import com.github.ness.check.combat.autoclick.AutoClickConfig;
import com.github.ness.check.movement.OmniSprint;
import com.github.ness.check.movement.fly.FlyInvalidClientGravity;
import com.github.ness.check.packet.Freecam;
import com.github.ness.check.packet.MorePackets;
import com.github.ness.check.packet.Timer;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

@ConfHeader({"All configuration relating to specific checks"})
public interface AllChecksConfig {

    @ConfKey("autoclick")
    @SubSection
    AutoClickConfig autoClick();

    @ConfKey("autoclicker")
    @ConfComments({"", "AutoClicker", "A Simple Max CPS Check", "", "Performance impact: Minimal", "Effectiveness: Medium", ""})
    @SubSection
    MaxCPS.Config maxCps();

    @ConfKey("killaura")
    @ConfComments({"", "Check if a player is using ForceField / ClickAura / Killaura", "This Check detect only some old clients ", "", "Performance impact: low", "Effectiveness: Medium", ""})
    @SubSection
    Killaura.Config killaura();

    @ConfKey("playeresp")
    @ConfComments({"", "Hide players that a player can't see", "", "Performance impact: Medium", "Effectiveness: High", ""})
    @SubSection
    PlayerESP.Config playerESP();

    @ConfKey("morepackets")
    @ConfComments({"", "Check if a player sends too many packets.", "This blocks some Regen, Nuker and some ServerCrasher exploits", "", "Performance impact: low", "Effectiveness: High", ""})
    @SubSection
    MorePackets.Config morePackets();

    @ConfKey("freecam")
    @SubSection
    Freecam.Config freecam();

    @ConfKey("timer")
    @ConfComments({"", "Check how many packets a player sends e", "", "Performance impact: low", "Effectiveness: High", ""})
    @SubSection
    Timer.Config timer();

    @ConfKey("client-gravity-fly")
    @ConfComments({"", "Check if a player is editing his next y gravity value.", "", "Performance impact: low", "Effectiveness: High", ""})
    @SubSection
    FlyInvalidClientGravity.Config flyInvalidClientGravity();

    @ConfKey("vertical-velocity")
    @ConfComments({"", "Check if a player is editing vertical velocity.", "", "Performance impact: low", "Effectiveness: Medium", ""})
    @SubSection
    VerticalVelocity.Config verticalVelocity();

    @ConfKey("omnisprint")
    @ConfComments({"", "Check if a player is sprinting backwards.", "", "Performance impact: low", "Effectiveness: High", ""})
    @SubSection
    OmniSprint.Config omniSprint();

    @ConfKey("killaurahitmissratio")
    @ConfComments({"", "Check the percentage hits/swings of a specific playe", "", "Performance impact: low", "Effectiveness: High", ""})
    @SubSection
    KillauraHitMissRatio.Config killauraHitMissRatio();
}

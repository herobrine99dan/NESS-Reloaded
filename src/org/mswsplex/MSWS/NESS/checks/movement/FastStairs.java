package org.mswsplex.MSWS.NESS.checks.movement;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.MovementPlayerData;
import org.mswsplex.MSWS.NESS.Utilities;
import org.mswsplex.MSWS.NESS.Utility;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class FastStairs {
	protected static List<String> checks = Arrays.asList("0.419", "0.333", "0.248");

	public static void Check(PlayerMoveEvent e) {
		/*
		 * Player p = e.getPlayer(); Location from = e.getFrom(); Location to =
		 * e.getTo(); String blockName =
		 * Utilities.getPlayerUnderBlock(p).getType().name(); if
		 * (!blockName.contains("STAIR")){ return; } if(Utility.hasflybypass(p)) {
		 * return; }
		 * if(!(p.getFallDistance()==0)||p.hasPotionEffect(PotionEffectType.SPEED)) {
		 * return; } double distance =
		 * Utility.around(Math.abs(Utility.getMaxSpeed(e.getFrom(), e.getTo())), 6);
		 * double ydist =
		 * to.getY()-from.getY();//!checks.contains(Double.toString(ydist)) if (distance
		 * > 0.54 && (ydist == 0.0 || ydist == 0.5)) { //WarnHacks.warnHacks(p,
		 * "FastStairs", 10, -1.0D, 88,"FastStairs A",false); if(NESS.main.devMode) {
		 * p.sendMessage("Dist: " + distance + " YDist: " + ydist); } }
		 */
		Player p = e.getPlayer();
		if (Utility.hasflybypass(p) || p.getFallDistance() != 0)
			return;
		Location to = e.getTo();
		Location from = e.getFrom();
		MovementPlayerData mp = MovementPlayerData.getInstance(p);
		//String blockName = to.subtract(0, 0.0001, 0).getBlock().getType().name().toLowerCase();
		String blockName = Utilities.getPlayerUnderBlock(p).getType().name().toLowerCase();
		if (!blockName.contains("stair"))
			return;
		double distance = Utility.getMaxSpeed(from, to);
		double ydist = to.getY()-from.getY();
		if (distance > 0.4 && mp.DistanceFastStairs > distance) {
			//p.sendMessage("Dist: " + distance + " YDist: " + ydist);
			WarnHacks.warnHacks(p, "FastStairs", 10, -1.0D, 88, "FastStairs A", false);
		}
		mp.DistanceFastStairs = distance;
	}
}

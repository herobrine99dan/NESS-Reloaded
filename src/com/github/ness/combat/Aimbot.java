package com.github.ness.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.MovementPlayerData;
import com.github.ness.Utilities;
import com.github.ness.Utility;
import com.github.ness.WarnHacks;

public class Aimbot {
	private static final Map<UUID, List<Float>> pitchdelta = new HashMap<UUID, List<Float>>();
	private static final Map<UUID, Float> lastmcdpitch = new HashMap<UUID, Float>();

	public static void Check(PlayerMoveEvent e) {
		int samples = 23;
		int pitchlimit = 10;
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		float deltaPitch = e.getTo().getPitch() - e.getFrom().getPitch();
		List<Float> lastDeltaPitches = pitchdelta.getOrDefault(uuid, new ArrayList<>());

		// ignore if deltaPitch is 0 or >= 10 or if pitch is +/-90.
		if (deltaPitch != 0 && Math.abs(deltaPitch) <= pitchlimit && Math.abs(e.getTo().getPitch()) != 90) {
			lastDeltaPitches.add(Math.abs(deltaPitch));
		}

		if (lastDeltaPitches.size() >= samples) {
			float deltaPitchGCD = Utility.mcdRational(lastDeltaPitches);
			float lastDeltaPitchGCD = lastmcdpitch.getOrDefault(uuid, deltaPitchGCD);
			float gcdDiff = Math.abs(deltaPitchGCD - lastDeltaPitchGCD);
			// if GCD is significantly different or if GCD is practically unsolvable
			if (gcdDiff > 0.001 || deltaPitchGCD < 0.00001) {
				WarnHacks.warnHacks(e.getPlayer(), "Aimbot", 5, -1.0D, 2, "PitchPattern", false);
			}
			lastDeltaPitches.clear();
			lastmcdpitch.put(uuid, deltaPitchGCD);
		}

		pitchdelta.put(uuid, lastDeltaPitches);
	}

	public static void Check1(PlayerMoveEvent e) {
		float yawChange = Math.abs(e.getTo().getYaw() - e.getFrom().getYaw());
		float pitchChange = Math.abs(e.getTo().getPitch() - e.getFrom().getPitch());
		if (yawChange >= 1.0f && yawChange % 0.1f == 0.0f) {
			WarnHacks.warnHacks(e.getPlayer(), "Aimbot", 2, -1.0D, 2, "PerfectAura", false);
		} else if (yawChange > 1.0f && yawChange % 0.1f == 0.0f) {
			WarnHacks.warnHacks(e.getPlayer(), "Aimbot", 3, -1.0D, 2, "PerfectAura", false);
		}
	}

	public static void Check2(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (event.getFrom().getY() >= event.getTo().getY()) {
			return;
		}
		if (Utilities.isOnStairs(player) || Utilities.isInLiquid(player) || Utilities.isOnWeb(player)) {
			return;// data.hasBeenDamaged()
		}
		if (player.getAllowFlight()) {
			return;
		}
		if (Utility.isOnSlime(player) || Utilities.isOnIce(player, true)) {
			return;
		}
		final double yaw = Math.abs(event.getFrom().getYaw() - event.getTo().getYaw());
		if (yaw > 0.0 && yaw < 360.0) {
			if (yaw >= 5.0) {
				if (yaw % 1.0f == 0.0f) {
					WarnHacks.warnHacks(event.getPlayer(), "Aimbot", 3, -1.0D, 2, "PerfectAura", false);
				}
			}
		}
	}

	public static void Check3(PlayerMoveEvent event) {

	}
}
package com.github.ness.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.MovementPlayerData;
import com.github.ness.NessPlayer;
import com.github.ness.Utilities;
import com.github.ness.Utility;
import com.github.ness.Violation;

public class Aimbot extends AbstractCheck<PlayerMoveEvent> {
	
	public Aimbot(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(PlayerMoveEvent e) {
       Check(e);
       Check1(e);
       Check2(e);
	}

	private static final Map<UUID, List<Float>> pitchdelta = new HashMap<UUID, List<Float>>();
	private static final Map<UUID, Float> lastmcdpitch = new HashMap<UUID, Float>();

	public void Check(PlayerMoveEvent e) {
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
				manager.getPlayer(e.getPlayer()).setViolation(new Violation("Aimbot"));
			}
			lastDeltaPitches.clear();
			lastmcdpitch.put(uuid, deltaPitchGCD);
		}

		pitchdelta.put(uuid, lastDeltaPitches);
	}

	public void Check1(PlayerMoveEvent e) {
		float yawChange = Math.abs(e.getTo().getYaw() - e.getFrom().getYaw());
		float pitchChange = Math.abs(e.getTo().getPitch() - e.getFrom().getPitch());
		if (yawChange >= 1.0f && yawChange % 0.1f == 0.0f) {
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("Aimbot"));
		} else if (yawChange > 1.0f && yawChange % 0.1f == 0.0f) {
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("Aimbot"));
		}
	}

	public void Check2(PlayerMoveEvent event) {
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
					//WarnHacks.warnHacks(event.getPlayer(), "Aimbot", 3, -1.0D, 2, "PerfectAura", false);
					manager.getPlayer(event.getPlayer()).setViolation(new Violation("Aimbot"));
				}
			}
		}
		return;
	}
}
package com.github.ness.check;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Aimbot extends AbstractCheck<PlayerMoveEvent> {

	public Aimbot(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check(e);
		Check1(e);
	}

	/**
	 * All changes in pitch should be divisible by a constant. That constant is
	 * determined by your mouse sensitivity in game. By calculating the gcd and
	 * finding that constant, you can detect changes in pitch variation.
	 * This Check was made by Islandscout! (@Islandscout#2588 on Discord)
	 * His AntiCheat: Hawk AntiCheat https://www.spigotmc.org/resources/hawk-anticheat-mc-1-7-10-1-8-8.40343/
	 * 
	 */
	public boolean Check(PlayerMoveEvent e) {
		int samples = 23;
		int pitchlimit = 10;
		Player p = e.getPlayer();
		NessPlayer player = manager.getPlayer(p);
		if (player == null) {
			return false;
		}
		float deltaPitch = e.getTo().getPitch() - e.getFrom().getPitch();
		final List<Float> lastDeltaPitches = player.getPitchdelta();

		// ignore if deltaPitch is 0 or >= 10 or if pitch is +/-90.
		if (deltaPitch != 0 && Math.abs(deltaPitch) <= pitchlimit && Math.abs(e.getTo().getPitch()) != 90) {
			lastDeltaPitches.add(Math.abs(deltaPitch));
		}

		if (lastDeltaPitches.size() >= samples) {
			float deltaPitchGCD = Utility.mcdRational(lastDeltaPitches);
			float lastmcdpitch = player.getLastmcdpitch();
			float lastDeltaPitchGCD = (lastmcdpitch != Float.MIN_VALUE) ? lastmcdpitch : deltaPitchGCD;
			float gcdDiff = Math.abs(deltaPitchGCD - lastDeltaPitchGCD);
			// if GCD is significantly different or if GCD is practically unsolvable
			if (gcdDiff > 0.001 || deltaPitchGCD < 0.00001) {
				manager.getPlayer(e.getPlayer()).setViolation(new Violation("Aimbot", "PitchPattern"));
				try {
					ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
							.getConfigurationSection("cancel");
					if (manager.getPlayer(p).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()),
							0) > cancelsec.getInt("vl", 10)) {
						e.setCancelled(true);
					}
				} catch (Exception ex) {
				}
				return true;
			}
			lastDeltaPitches.clear();
			player.setLastmcdpitch(deltaPitchGCD);
		}
		return false;

	}

	/**
	 * Check for some Aimbot Pattern
	 */
	public boolean Check1(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		float yawChange = Math.abs(e.getTo().getYaw() - e.getFrom().getYaw());
		float pitchChange = Math.abs(e.getTo().getPitch() - e.getFrom().getPitch());
		if (yawChange >= 1.0f && yawChange % 0.1f == 0.0f) {
			try {
				ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
						.getConfigurationSection("cancel");
				if (manager.getPlayer(p).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()),
						0) > cancelsec.getInt("vl", 10)) {
					e.setCancelled(true);
				}
			} catch (Exception ex) {
			}
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("Aimbot", "PerfectAura"));
			return true;
		} else if (pitchChange >= 1.0f && pitchChange % 0.1f == 0.0f) {
			try {
				ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
						.getConfigurationSection("cancel");
				if (manager.getPlayer(p).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()),
						0) > cancelsec.getInt("vl", 10)) {
					e.setCancelled(true);
				}
			} catch (Exception ex) {
			}
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("Aimbot", "PerfectAura1"));
			return true;
		}
		return false;
	}

}
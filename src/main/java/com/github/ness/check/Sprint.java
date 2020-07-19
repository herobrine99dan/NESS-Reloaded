package com.github.ness.check;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Sprint extends AbstractCheck<PlayerMoveEvent> {
	public HashMap<String, Integer> sprintcheck = new HashMap<String, Integer>();

	public Sprint(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check1(e);
	}

	private void checkfailed(PlayerMoveEvent e, Player p, String module, String check) {
		int failed = sprintcheck.getOrDefault(p.getName(), 0);
		sprintcheck.put(p.getName(), failed + 1);
		if (failed > 5) {
			manager.getPlayer(p).setViolation(new Violation("Sprint", check));
			if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
				e.setCancelled(true);
			}
			// MSG.tell(p, "Sprint(Omni)[nord], VL: orec".replace("nord",
			// yaw).replace("orec", failed+""));
		}
	}

	public void Check1(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (p.hasPotionEffect(PotionEffectType.SPEED) || Utility.hasflybypass(p)) {
			return;
		}
		Location from = e.getFrom();
		Location to = e.getTo();
		if (!p.isSprinting() && p.getNearbyEntities(3.5, 3.5, 3.5).isEmpty()
				|| !this.manager.getPlayer(p).isTeleported()) {
			double distance = Utility.getMaxSpeed(from, to);
			if (distance > 0.280 && !p.isFlying()) {
				checkfailed(e, p, "Speed", "HighDistance");
			}
		}
	}
}

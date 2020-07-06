package com.github.ness.check;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.check.killaura.heuristics.KillauraPlayerData;

public class KillauraFalseAngle extends AbstractCheck<EntityDamageByEntityEvent> {
	public HashMap<Player, Entity> lastEntityHit = new HashMap<Player, Entity>();
	public HashMap<String, String> mobinfront = new HashMap<String, String>();

	public KillauraFalseAngle(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
	}

	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
		Check(e);
	}

	void Check(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			KillauraPlayerData kplayer = KillauraPlayerData.getInstance(p);
			List<Double> data = kplayer.getAnglesList();
			data.add(isLookingAt(p, e.getEntity().getLocation()));
			if (data.size() >= 5) {
				double result = 0;
				for (Double d : data) {
					result += d;
				}
				result = result / data.size();
				if (this.manager.getPlayer(p).isDevMode()) {
					p.sendMessage("FalseAngleCheck: Result " + result + " Size: " + data.size());
				}
				if (result < 0.8) {
					punish(e, p, "FalseAngle: " + result);
				}
				data.clear();
			}
			kplayer.setAnglesList(data);
		}
	}

	private void punish(EntityDamageByEntityEvent e, Player p, String module) {
		if (manager.getPlayer(p).shouldCancel(e, "Killaura")) {
			e.setCancelled(true);
		}
		manager.getPlayer(p).setViolation(new Violation("Killaura", module));
	}

	private double isLookingAt(Player player, Location target) {
		Location eye = player.getEyeLocation();
		Vector toEntity = target.toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(eye.getDirection());

		return dot;// dot > 0.99D
	}
}

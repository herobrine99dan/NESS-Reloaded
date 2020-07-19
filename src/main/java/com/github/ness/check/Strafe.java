package com.github.ness.check;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Strafe extends AbstractCheck<PlayerMoveEvent> {

	public Strafe(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location to = e.getTo();
		Location from = e.getFrom();
		NessPlayer np = this.manager.getPlayer(p);
		double distX = to.getX() - from.getX();
		double distZ = to.getZ() - from.getZ();
		double dist = (distX * distX) + (distZ * distZ);
		double lastDist = np.lastStrafeDist;
		np.lastStrafeDist = dist;
		if (dist == 0.0) {
			return;
		}
		if (Utility.getMaterialName(to).contains("water") || Utility.getMaterialName(to).contains("lava")
				|| Utility.getMaterialName(from).contains("water") || Utility.getMaterialName(from).contains("water")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("ladder") || Utility.getMaterialName(from).contains("ladder")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("web") || Utility.getMaterialName(from).contains("web")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("vine") || Utility.getMaterialName(from).contains("vine")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("fence") || Utility.getMaterialName(from).contains("fence")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("slab") || Utility.getMaterialName(from).contains("slab")) {
			return;
		}
		if (!Utility.isMathematicallyOnGround(to.getY()) && !Utility.isMathematicallyOnGround(to.getY())) {
			if (lastDist == dist && dist < 1) {
				this.manager.getPlayer(p).setViolation(new Violation("Strafe","Dist: " + dist));
				if(manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	public void Check(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location to = e.getTo();
		Location from = e.getFrom();
		NessPlayer np = this.manager.getPlayer(p);
		double distX = to.getX() - from.getX();
		double distZ = to.getZ() - from.getZ();
		double dist = (distX * distX) + (distZ * distZ);
		double lastDist = np.lastStrafeDist;
		np.lastStrafeDist = dist;
		if (dist == 0.0) {
			return;
		}
		if (Utility.getMaterialName(to).contains("water") || Utility.getMaterialName(to).contains("lava")
				|| Utility.getMaterialName(from).contains("water") || Utility.getMaterialName(from).contains("water")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("ladder") || Utility.getMaterialName(from).contains("ladder")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("web") || Utility.getMaterialName(from).contains("web")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("vine") || Utility.getMaterialName(from).contains("vine")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("fence") || Utility.getMaterialName(from).contains("fence")) {
			return;
		}
		if (!Utility.isMathematicallyOnGround(to.getY()) && !Utility.isMathematicallyOnGround(to.getY())) {
			if (lastDist == dist && dist < 1) {
				this.manager.getPlayer(p).setViolation(new Violation("Strafe","Dist: " + dist));
				if(manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			}
		}
	}
	
}

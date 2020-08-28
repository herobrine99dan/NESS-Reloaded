package com.github.ness.check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class Fly extends AbstractCheck<PlayerMoveEvent> {

	double maxInvalidVelocity;

	public Fly(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		this.maxInvalidVelocity = this.manager.getNess().getNessConfig().getCheck(this.getClass())
				.getDouble("maxinvalidvelocity", 0.9);
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check1(e);
		Check2(e);
		Check3(e);
	}

	protected List<String> bypasses = Arrays.asList("slab", "stair", "snow", "bed", "skull", "step", "slime");

	public void punish(PlayerMoveEvent e, String module) {
		if (!Utility.hasflybypass(e.getPlayer())) {
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("Fly", module), e);
		}
	}

	/**
	 * Check for abnormal ground packet
	 * 
	 * @param e
	 */
	public void Check1(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (Bukkit.getVersion().contains("1.8")) {
			return;
		}
		if (Utility.getMaterialName(e.getTo().clone().add(0, -0.5, 0)).contains("lily")
				|| Utility.getMaterialName(e.getTo().clone().add(0, -0.5, 0)).contains("carpet")) {
			return;
		}
		if (Utility.specificBlockNear(e.getTo(), "lily") || Utility.specificBlockNear(e.getTo(), "snow")) {
			return;
		}
		if (Utility.specificBlockNear(e.getTo(), "carpet")) {
			return;
		}
		if (Utility.getMaterialName(e.getTo().clone()).contains("lily")
				|| Utility.getMaterialName(e.getTo().clone()).contains("carpet")) {
			return;
		}
		if (player.getNearbyEntities(2, 2, 2).isEmpty() && !Utility.hasflybypass(player)
				&& !this.manager.getPlayer(player).isTeleported()) {
			if (player.isOnline() && !Utility.hasBlock(player, "slime") && !player.isInsideVehicle()) {
				if (player.isOnGround() && !Utility.groundAround(e.getTo())) {
					punish(e, "FalseGround");
				} else if (player.isOnGround() && !Utility.isMathematicallyOnGround(e.getTo().getY())) {
					punish(e, "FalseGround1");
				}
			}

		}
	}

	public void Check2(PlayerMoveEvent e) {
		NessPlayer np = this.manager.getPlayer(e.getPlayer());
		Player p = e.getPlayer();
		double y = np.getMovementValues().yDiff;
		double yresult = y - p.getVelocity().getY();
		if (Utility.hasflybypass(p) || Utility.hasBlock(p, "slime") || p.getAllowFlight()
				|| Utility.specificBlockNear(e.getTo().clone().add(0, -0.3, 0), "lily")) {
			return;
		}
		double max = maxInvalidVelocity;
		float pingresult = Utility.getPing(p) / 100;
		float toAdd = pingresult / 4;
		max += toAdd;
		if (Math.abs(yresult) > max && !manager.getPlayer(e.getPlayer()).isTeleported()) {
			punish(e, "InvalidVelocity: " + yresult);
		}
	}

	public void Check3(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location to = event.getTo().clone();
		Location from = event.getFrom().clone();
		double yDiff = event.getTo().getY() - event.getFrom().getY();
		if (Utility.getMaterialName(event.getTo().clone().add(0, -0.3, 0)).contains("slab")
				|| event.getTo().getBlock().isLiquid()
				|| event.getTo().clone().add(0, 1.8, 0).getBlock().getType().isSolid()
				|| event.getTo().clone().add(0.3, 1.8, 0.3).getBlock().getType().isSolid()
				|| event.getTo().clone().add(-0.3, 1.8, -0.3).getBlock().getType().isSolid()
				|| event.getTo().clone().add(0, 2, 0).getBlock().getType().isSolid()
				|| event.getTo().clone().add(0.3, 2, 0.3).getBlock().getType().isSolid()
				|| event.getTo().clone().add(-0.3, 2, -0.3).getBlock().getType().isSolid()
				|| Utility.specificBlockNear(event.getTo(), "liquid") || Utility.hasflybypass(player)
				|| Utility.specificBlockNear(player.getLocation(), "snow")
				|| Utility.specificBlockNear(player.getLocation(), "chest")
				|| Utility.specificBlockNear(player.getLocation(), "ladder")
				|| Utility.specificBlockNear(player.getLocation(), "pot")
				|| Utility.specificBlockNear(player.getLocation(), "bed")
				|| Utility.specificBlockNear(player.getLocation(), "detector")
				|| Utility.specificBlockNear(player.getLocation(), "stair")
				|| Utility.getMaterialName(to.add(0, -1, 0)).contains("chest")
				|| Utility.getMaterialName(from.add(0, -1, 0)).contains("chest")
				|| Utility.getMaterialName(to.add(0, 1.8, 0)).contains("chorus")
				|| Utility.getMaterialName(from.add(0, 1.6, 0)).contains("chorus")
				|| Utility.getMaterialName(to).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(from).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(to).toLowerCase().contains("vine")
				|| Utility.getMaterialName(from).toLowerCase().contains("vine")
				|| Utility.getMaterialName(to).toLowerCase().contains("sea")
				|| Utility.getMaterialName(from).toLowerCase().contains("sea")
				|| Utility.getMaterialName(to.clone().add(0, 0.3, 0)).toLowerCase().contains("sea")
				|| Utility.getMaterialName(to.clone().add(0, -0.2, 0)).toLowerCase().contains("sea")
				|| Utility.getMaterialName(to).toLowerCase().contains("pot")
				|| Utility.getMaterialName(from).toLowerCase().contains("pot")
				|| Utility.getMaterialName(to.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(from.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(to.clone().add(0, 0.5, 0)).toLowerCase().contains("bed")
				|| Utility.getMaterialName(from.clone().add(0, 0.5, 0)).toLowerCase().contains("bed")
				|| Utility.getMaterialName(to.add(0, -1, 0)).contains("detector")
				|| Utility.getMaterialName(from.add(0, -1, 0)).contains("detector")
				|| Utility.specificBlockNear(to.clone(), "ice")) {
			return;
		}
		// !player.getNearbyEntities(4, 4, 4).isEmpty()
		if (yDiff > 0 && !player.isInsideVehicle()) {
			if (player.getVelocity().getY() == 0.42f && !Utility.isMathematicallyOnGround(event.getTo().getY())
					&& Utility.isMathematicallyOnGround(event.getFrom().getY())) {
				double yResult = Math.abs(yDiff - player.getVelocity().getY());
				if (yResult != 0.0 && this.manager.getPlayer(player).nanoTimeDifference(PlayerAction.DAMAGE) > 1000) {
					punish(event, "InvalidJumpMotion yResult: " + yResult + "  yDiff: " + yDiff);
				}
			}
		}
	}
}

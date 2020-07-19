package com.github.ness.check;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class FastSneak extends AbstractCheck<PlayerMoveEvent> {

	public FastSneak(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check(e);
		Check1(e);
	}

	void Check(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		if (p.isSneaking() && !Utility.hasflybypass(p) && !p.isFlying() && p.isSprinting()) {
			np.setViolation(new Violation("FastSneak", "Sneaking while Sprinting"));
			np.shouldCancel(e, "FastSneak");
		}
	}

	void Check1(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
<<<<<<< HEAD
		if (p.isSneaking() && !Utility.hasflybypass(p) && !p.isSprinting() && Utility.isOnGround(e.getTo())) {
			ConfigurationSection config = this.manager.getNess().getNessConfig().getCheck(this.getClass());
			if (Utility.getMaxSpeed(e.getFrom(), e.getTo()) > config.getDouble("maxdistance",0.15)) {
=======
		if (p.isSneaking() && !Utility.hasflybypass(p) && !p.isSprinting()) {
			if (Utility.getMaxSpeed(e.getFrom(), e.getTo()) > 0.15) {
>>>>>>> parent of 2cf5ca9d... Adding new options for some checks
				np.setViolation(new Violation("FastSneak", "HighDistance"));
				np.shouldCancel(e, "FastSneak");
			}
		}
	}
}

package com.github.ness.check.combat;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class NoSlowDownBow extends AbstractCheck<EntityShootBowEvent> {

	public NoSlowDownBow(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityShootBowEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkEvent(EntityShootBowEvent e) {
		Check(e);
	}

	public void Check(EntityShootBowEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			Player o = (Player) e.getEntity();
			if (Utility.hasflybypass(o)) {
				return;
			}
			NessPlayer p = manager.getPlayer(o);
			double distance = p.getMovementValues().XZDiff;
			/*
			 * if (o.isSprinting() || failed==1) { e.setCancelled(true);
			 * checkfailed(o.getName()); }
			 */
			distance -= o.getVelocity().getX();
			distance -= o.getVelocity().getZ();
			if (distance > 0.23 || o.isSprinting()) {
				p.setViolation(new Violation("NoSlowDown", ""), e);
			}
		}
	}

}

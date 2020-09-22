package com.github.ness.check.combat;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

@Deprecated //This Check needs a complete recode
public class NoSlowDownBow extends AbstractCheck<EntityShootBowEvent> {

	public static final CheckInfo<EntityShootBowEvent> checkInfo = CheckInfo.eventOnly(EntityShootBowEvent.class);

	public NoSlowDownBow(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(EntityShootBowEvent e) {
		if (player().isNot(e.getEntity()))
			return;
		Check(e);
	}

	public void Check(EntityShootBowEvent e) {
		Player o = (Player) e.getEntity();
		if (Utility.hasflybypass(o)) {
			return;
		}
		NessPlayer p = player();
		double distance = p.getMovementValues().XZDiff;
		/*
		 * if (o.isSprinting() || failed==1) { e.setCancelled(true);
		 * checkfailed(o.getName()); }
		 */
		distance -= o.getVelocity().getX();
		distance -= o.getVelocity().getZ();
		if (distance > 0.25 || o.isSprinting()) {
			p.setViolation(new Violation("NoSlowDown", ""), e);
		}
	}
}

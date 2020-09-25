package com.github.ness.check.combat;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;

@Deprecated //This Check needs a complete recode
public class NoSlowBow extends ListeningCheck<EntityShootBowEvent> {

	public static final ListeningCheckInfo<EntityShootBowEvent> checkInfo = CheckInfos.forEvent(EntityShootBowEvent.class);

	public NoSlowBow(ListeningCheckFactory<?, EntityShootBowEvent> factory, NessPlayer player) {
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
			flagEvent(e);
			//if(p.setViolation(new Violation("NoSlowDown", ""))) e.setCancelled(true);
		}
	}
}

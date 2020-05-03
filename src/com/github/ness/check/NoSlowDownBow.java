package com.github.ness.check;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.Utility;
import com.github.ness.Violation;

public class NoSlowDownBow extends AbstractCheck<EntityShootBowEvent>{

	public NoSlowDownBow(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityShootBowEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(EntityShootBowEvent e) {
       Check(e);
	}
	
	public void Check(EntityShootBowEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			Player o = (Player) e.getEntity();
			if(Utility.hasflybypass(o)) {
				return;
			}
            NessPlayer p = manager.getPlayer(o);
			double distance = p.getDistance();
			/*
			 * if (o.isSprinting() || failed==1) { e.setCancelled(true);
			 * checkfailed(o.getName()); }
			 */
			if (distance > 0.2||o.isSprinting()) {
				p.setViolation(new Violation("NoSlowDown"));
			}
		}
	}
	
}

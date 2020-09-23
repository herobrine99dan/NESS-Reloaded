package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class FlyInvalidGravity extends AbstractCheck<PlayerMoveEvent> {

    double maxInvalidVelocity;
    
	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo
			.eventOnly(PlayerMoveEvent.class);

	public FlyInvalidGravity(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
        this.maxInvalidVelocity = this.ness().getNessConfig().getCheck(this.getClass())
                .getDouble("maxinvalidvelocity", 0.9);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent e) {
        Check(e);
    }

    /**
     * Check for Invalid Gravity
     *
     * @param e
     */
    public void Check(PlayerMoveEvent e) {
        NessPlayer np = this.player();
        Player p = e.getPlayer();
        double y = np.getMovementValues().yDiff;
        double yresult = y - p.getVelocity().getY();
        if (Utility.hasflybypass(p) || Utility.hasBlock(p, "slime") || p.getAllowFlight()
                || Utility.specificBlockNear(e.getTo().clone().add(0, -0.3, 0), "lily") || p.isInsideVehicle()) {
            return;
        }
        double max = maxInvalidVelocity;
        float pingresult = Utility.getPing(p) / 100;
        float toAdd = pingresult / 6;
        max += toAdd;
        if (np.nanoTimeDifference(PlayerAction.VELOCITY) < 2500) {
            y -= Math.abs(np.velocity.getY());
        }
        if (Math.abs(yresult) > max && !np.isTeleported()) {
        	if(player().setViolation(new Violation("Fly", "InvalidVelocity: " + yresult))) e.setCancelled(true);
        }
    }
}

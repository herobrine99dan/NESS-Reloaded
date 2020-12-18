package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class FlyInvalidServerGravity extends ListeningCheck<PlayerMoveEvent> {

    double maxInvalidVelocity;
    
	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos
			.forEvent(PlayerMoveEvent.class);

	public FlyInvalidServerGravity(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
        this.maxInvalidVelocity = this.ness().getMainConfig().getCheckSection().flyInvalidServerGravity().maxGravity();
	}
	
	@Override
	protected boolean shouldDragDown() {
		return true;
	}
	
	public interface Config {
		@DefaultDouble(1.5)
		double maxGravity();
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
        MovementValues values = np.getMovementValues();
        double y = values.getyDiff();
        double yresult = y - p.getVelocity().getY();
        if (Utility.hasflybypass(p) || values.isAroundSlime() || p.getAllowFlight()
                || values.isAroundLily() || Utility.hasVehicleNear(p, 3)) {
            return;
        }
        double max = maxInvalidVelocity;
        float pingresult = Utility.getPing(p) / 100;
        float toAdd = pingresult / 6;
		double jumpBoost = Utility.getPotionEffectLevel(p, PotionEffectType.JUMP);
        max += toAdd;
        if(!e.getTo().getBlock().isLiquid()) {
        	y -= jumpBoost * (y / 2);
        }
        if (np.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2500) {
            y -= Math.abs(np.getLastVelocity().getY());
        }
        if (Math.abs(yresult) > max && !np.isTeleported()) {
        	flagEvent(e, " " + yresult);
        	//if(player().setViolation(new Violation("Fly", "InvalidVelocity: " + yresult))) e.setCancelled(true);
        }
    }
}

package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class FastSneak extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos
			.forEvent(PlayerMoveEvent.class);

	public FastSneak(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        NessPlayer nessPlayer = this.player();
        float dist = (float) Math.abs(nessPlayer.getMovementValues().XZDiff); // Our XZ Distance
        if (nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) < 1300) {
            dist -= Math.abs(nessPlayer.velocity.getX()) + Math.abs(nessPlayer.velocity.getZ());
        }
        double walkSpeed = p.getWalkSpeed() * 0.85;
        dist -= (dist / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
        walkSpeed += Math.abs(p.getVelocity().getY()) * 0.4;
        if (dist > walkSpeed && p.isSneaking() && !Utility.hasflybypass(p)) {
            if (Math.abs(nessPlayer.getMovementValues().xDiff) > walkSpeed
                    || Math.abs(nessPlayer.getMovementValues().zDiff) > walkSpeed) {
            	flagEvent(event);
            	//if(player().setViolation(new Violation("FastSneak", "Dist: " + dist))) event.setCancelled(true);
            }
        }
    }

}

package com.github.ness.check.movement;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class FastSneak extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo
			.eventOnly(PlayerMoveEvent.class);

	public FastSneak(CheckFactory<?> factory, NessPlayer player) {
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
                nessPlayer.setViolation(new Violation("FastSneak", "Dist: " + dist), event);
            }
        }
    }

}

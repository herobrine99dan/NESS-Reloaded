package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.ReflectionUtility;
import com.github.ness.utility.Utility;

public class FlyHighJump extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo
			.eventOnly(PlayerMoveEvent.class);

	private double flyYSum;
	public FlyHighJump(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent e) {
        NessPlayer nessPlayer = this.player();
        Player p = e.getPlayer();
        double y = nessPlayer.getMovementValues().yDiff;
        if (Utility.isMathematicallyOnGround(e.getTo().getY()) || Utility.hasflybypass(p)
                || Utility.hasBlock(p, "slime") || p.getAllowFlight() || Utility.isInWater(p)
                || Utility.specificBlockNear(e.getTo().clone(), "lily")
                || Utility.specificBlockNear(e.getTo().clone(), "sea")
                || Utility.specificBlockNear(e.getTo().clone(), "slab")
                || Utility.specificBlockNear(e.getTo().clone(), "stair")
                || Utility.specificBlockNear(e.getTo().clone(), "water")
                || Utility.specificBlockNear(e.getTo().clone(), "lava")
                || ReflectionUtility.getBlockName(p, ImmutableLoc.of(p.getLocation().clone().add(0, -0.5, 0)))
                .contains("scaffolding")
                || ReflectionUtility.getBlockName(p, ImmutableLoc.of(p.getLocation().clone().add(0, 0.5, 0)))
                .contains("scaffolding")
                || Utility.getMaterialName(e.getTo()).contains("ladder")
                || Utility.getMaterialName(e.getTo()).contains("snow") || Utility.specificBlockNear(e.getTo(), "snow")
                || Utility.specificBlockNear(e.getTo(), "ladder") || Utility.specificBlockNear(e.getTo(), "vine")
                || nessPlayer.isTeleported()) {
            flyYSum = 0;
            return;
        }
        if (nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) < 1500) {
            y -= Math.abs(nessPlayer.velocity.getY());
        }
        if (y > 0) {
            flyYSum += y;
            double max = 1.30;
            double jumpBoost = Utility.getPotionEffectLevel(p, PotionEffectType.JUMP);
            max += jumpBoost * (max / 2);
            if (flyYSum > max && p.getVelocity().getY() < 0) {
                nessPlayer.setViolation(new Violation("Fly", "HighJump ySum: " + flyYSum), e);
            }
        }
    }

}

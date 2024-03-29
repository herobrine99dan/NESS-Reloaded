package com.github.ness.check.combat;

import java.time.Duration;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.PeriodicTaskInfo;
import com.github.ness.utility.LongRingBuffer;

public class KillauraAnglePattern extends ListeningCheck<EntityDamageByEntityEvent> {
	private final double anglePatternMaxPrecision;
	private final LongRingBuffer anglePatternList = new LongRingBuffer(10);
	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEventWithTask(EntityDamageByEntityEvent.class, PeriodicTaskInfo.asyncTask(Duration.ofMillis(4000)));

	public KillauraAnglePattern(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
		super(factory, player);
		this.anglePatternMaxPrecision = this.ness().getMainConfig().getCheckSection().killaura()
				.anglePatternMaxPrecision();
	}

	@Override
	protected void checkAsyncPeriodic() {
		if (anglePatternList.size() > 1) {// Prevents math errors
			double averageAngle = anglePatternList.average() / 10000;
			double standardDeviationSample = ((anglePatternList.standardDeviation() / 10000) / averageAngle) * 100;
			player().sendDevMessage("standardDeviationSample: " + (float) standardDeviationSample);
			if (standardDeviationSample < anglePatternMaxPrecision
					&& Math.abs(this.player().getMovementValues().getYawDiff()) > 10) { // If you don't move, obviously you can flag
																						// the angle difference is
																						// always 0
				this.flag("AnglePatternList");
			}
			anglePatternList.clear();
		}
	}

	@Override
	protected void checkEvent(final EntityDamageByEntityEvent e) {
		if (player().isNot(e.getDamager())) {
                    return;
                }
		if (!(e.getEntity() instanceof LivingEntity)) {
			return;
		}
		Vector playerLookDir = this.player().getMovementValues().getDirection();
		Vector playerEyeLoc = this.player().getBukkitPlayer().getEyeLocation().toVector();
		Vector entityLoc = e.getEntity().getLocation().toVector();
		Vector playerEntityVec = entityLoc.subtract(playerEyeLoc);
		float angle = playerLookDir.angle(playerEntityVec);
		anglePatternList.add((long) (angle * 10000));
	}
}

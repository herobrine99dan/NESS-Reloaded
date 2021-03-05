package com.github.ness.check.combat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.PeriodicTaskInfo;
import com.github.ness.utility.MathUtils;

public class KillauraAnglePattern extends ListeningCheck<EntityDamageByEntityEvent> {
	private final double anglePatternMaxPrecision;
	private List<Float> anglePatternList = new ArrayList<Float>();
	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEventWithTask(EntityDamageByEntityEvent.class, PeriodicTaskInfo.asyncTask(Duration.ofMillis(4000)));

	public KillauraAnglePattern(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
		super(factory, player);
		this.anglePatternMaxPrecision = this.ness().getMainConfig().getCheckSection().killaura()
				.anglePatternMaxPrecision();
	}

	@Override
	protected void checkAsyncPeriodic() {
		if (anglePatternList.size() > 1) {//Prevents math errors
			double averageAngle = MathUtils.average(anglePatternList);
			double standardDeviationSample = (calculateSD(anglePatternList, false) * 100) / averageAngle;
			player().sendDevMessage("standardDeviationSample: " + (float) standardDeviationSample);
			if (standardDeviationSample < anglePatternMaxPrecision
					&& Math.abs(this.player().getMovementValues().getYawDiff()) > 10) { //If you don't move, obviously the angle difference is always 0
				this.flag("AnglePatternList");
			}
			anglePatternList.clear();
		}
	}

	@Override
	protected void checkEvent(final EntityDamageByEntityEvent e) {
		if (player().isNot(e.getDamager()))
			return;
		if (!(e.getEntity() instanceof LivingEntity)) {
			return;
		}
		Vector playerLookDir = this.player().getMovementValues().getDirection().toBukkitVector();
		Vector playerEyeLoc = this.player().getBukkitPlayer().getEyeLocation().toVector();
		Vector entityLoc = e.getEntity().getLocation().toVector();
		Vector playerEntityVec = entityLoc.subtract(playerEyeLoc);
		float angle = playerLookDir.angle(playerEntityVec);
		anglePatternList.add(angle);
	}

	private double calculateSD(List<Float> data, boolean population) {
		double sum = 0.0, standardDeviation = 0.0;
		int length = data.size();

		for (double num : data) {
			sum += num;
		}

		double mean = sum / length;

		for (double num : data) {
			standardDeviation += Math.pow(num - mean, 2);
		}
		int divider = population ? length - 1 : length;
		return Math.sqrt(standardDeviation / divider);
	}
}
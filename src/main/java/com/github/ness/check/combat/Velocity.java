package com.github.ness.check.combat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PeriodicTaskInfo;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class Velocity extends Check {

	public static final CheckInfo checkInfo = CheckInfos.withTask(PeriodicTaskInfo.syncTask(Duration.ofMillis(50)));
	private double minVelocityPercentage = 95;
	private double buffer;

	public Velocity(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.minVelocityPercentage = this.ness().getMainConfig().getCheckSection().verticalVelocity()
				.minVelocityPercentage();
	}

	public interface Config {
		@DefaultDouble(95)
		double minVelocityPercentage();
	}

	private List<Float> lastYDistances = new ArrayList<Float>();
	
	protected void checkSyncPeriodic() {
		
	}
	

}
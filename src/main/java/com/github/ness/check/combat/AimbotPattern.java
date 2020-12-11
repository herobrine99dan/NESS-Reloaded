package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.List;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;

public class AimbotPattern extends ListeningCheck<ReceivedPacketEvent> {

	List<Double> yawChanges;
	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos.forEvent(ReceivedPacketEvent.class);
	private static final int SIZE = 20;

	public AimbotPattern(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
		yawChanges = new ArrayList<Double>();
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent event) {
		NessPlayer player = this.player();
		yawChanges.add(player.getMovementValues().getYawDiff());
		if (yawChanges.size() > SIZE) {

		}
	}

	public double getAverage(List<Double> list) {
		double sum = 0;
		for (double d : list) {
			sum += d;
		}
		sum /= list.size();
		return sum;
	}

	public double findPatternNumber(List<Double> list) {
		double subtraction = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			double d = list.get(i);
			subtraction = Math.abs(subtraction - d);
		}
		return subtraction;
	}

}

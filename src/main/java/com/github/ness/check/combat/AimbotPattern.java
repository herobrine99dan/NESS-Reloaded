package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;

public class AimbotPattern extends AbstractCheck<ReceivedPacketEvent> {

	List<Double> yawChanges;
	public static final CheckInfo<ReceivedPacketEvent> checkInfo = CheckInfo.eventOnly(ReceivedPacketEvent.class);
	private static final int SIZE = 20;

	public AimbotPattern(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		yawChanges = new ArrayList<Double>();
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent event) {
		NessPlayer player = this.player();
		yawChanges.add(player.getMovementValues().yawDiff);
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

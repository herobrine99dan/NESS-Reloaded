package com.github.ness.check.packet;

import java.util.concurrent.TimeUnit;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.LongRingBuffer;
import com.github.ness.utility.Utility;

import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class Timer extends ListeningCheck<ReceivedPacketEvent> {
	private double MAX_PACKETS_PER_TICK = 1.07;

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEvent(ReceivedPacketEvent.class);
	private long lastDelay;
	private LongRingBuffer delay;
	private boolean negativeTimerEnabled = true;

	public Timer(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
		this.MAX_PACKETS_PER_TICK = this.ness().getMainConfig().getCheckSection().timer().maxpackets();
		this.delay = new LongRingBuffer(30);
		this.negativeTimerEnabled = this.ness().getMainConfig().getCheckSection().timer().negativetimer();
	}

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	public interface Config {
		@DefaultDouble(1.1)
		double maxpackets();

		@DefaultBoolean(false)
		boolean negativetimer();
	}

	/**
	 * Thanks to GladUrBad for a small hint
	 */
	@Override
	protected void checkEvent(ReceivedPacketEvent e) {
		NessPlayer nessPlayer = e.getNessPlayer();

		if (!e.getPacket().getName().toLowerCase().contains("position") || nessPlayer.isTeleported()) {
			return;
		}
		final long current = System.nanoTime();
		long result = (long) ((current - lastDelay) / 1e+6);

		delay.add(result);
		if (delay.size() < 40 || nessPlayer.isHasSetback()) {
			return;
		}
		final long average = delay.average();
		final float speed = 50.0f / (float) average;
		if (result > 1300|| average > 1300 || speed < 0.01) {
			delay.clear();
			return;
		}
		if (nessPlayer.isDebugMode()) {
			nessPlayer.sendDevMessage("Timer: " + speed + " Average: " + average + " Current: " + current);
		}
		nessPlayer.sendDevMessage("PlayerSpeed: " + speed + " Average: " + average);
		if (speed > MAX_PACKETS_PER_TICK) {
			this.flagEvent(e, "BasicTimer " + Utility.round(speed, 100));
		} else if ((speed > 0.2 && speed < 0.9) && negativeTimerEnabled) {
			this.flagEvent(e, "NegativeTimer " + Utility.round(speed, 100));
		}
		delay.clear();
		this.lastDelay = current;
	}
}

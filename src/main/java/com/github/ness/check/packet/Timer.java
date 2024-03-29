package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.utility.LongRingBuffer;

import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class Timer extends PacketCheck {
	private final double MAX_PACKETS_PER_TICK;

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	private long lastDelay = System.nanoTime();
	private LongRingBuffer delay;
	private double buffer;
	private final boolean negativeTimerEnabled;
	private final boolean useMedian;

	public Timer(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.MAX_PACKETS_PER_TICK = this.ness().getMainConfig().getCheckSection().timer().maxpackets();
		this.delay = new LongRingBuffer(this.ness().getMainConfig().getCheckSection().timer().delaysSize());
		this.negativeTimerEnabled = this.ness().getMainConfig().getCheckSection().timer().negativetimer();
		this.useMedian = this.ness().getMainConfig().getCheckSection().timer().useMedian();
	}

	public interface Config {
		@DefaultDouble(1.1)
		double maxpackets();

		@DefaultInteger(40)
		int delaysSize();

		@DefaultBoolean(false)
		boolean negativetimer();
		
		@DefaultBoolean(false)
		boolean useMedian();
	}

	/**
	 * Thanks to GladUrBad for a small hint
	 */
	@Override
	protected void checkPacket(Packet packet) {
		NessPlayer nessPlayer = player();
		if (!packet.getRawPacket().getClass().getSimpleName().toLowerCase().contains("position")) return;
		if (nessPlayer.isTeleported() || nessPlayer.isHasSetback()) {
			delay.clear();
			return;
		}
		final long current = System.nanoTime();
		long result = (long) ((current - lastDelay) / 1e+6);
		if (result > 5) {
			delay.add(result);
		}
		double average = delay.average();
		if(useMedian) {
			average = delay.median();
		}
		final float speed = 50.0f / (float) average;
		// nessPlayer.sendDevMessage("Average: " + average + " Speed: " + speed + "
		// size: " + delay.size());
		if (delay.size() > (this.ness().getMainConfig().getCheckSection().timer().delaysSize() - 1)) {
			if (speed > MAX_PACKETS_PER_TICK) {
				if (++buffer > 4) {
					this.flag("BasicTimer " + (float) speed);
				}
			} else if ((speed > 0.2 && speed < 0.9) && negativeTimerEnabled) {
				if (++buffer > 3) {
					this.flag("NegativeTimer " + (float) speed);
				}
			} else if (buffer > 0) {
				buffer -= 0.5;
			}
			nessPlayer.updateTimerTicks(speed);
		}
		this.lastDelay = current;
	}
}

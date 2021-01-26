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
	private double MAX_PACKETS_PER_TICK = 1.07;

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	private long lastDelay = System.nanoTime();
	private LongRingBuffer delay;
	private double buffer;
	private boolean negativeTimerEnabled = true;

	public Timer(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.MAX_PACKETS_PER_TICK = this.ness().getMainConfig().getCheckSection().timer().maxpackets();
		this.delay = new LongRingBuffer(this.ness().getMainConfig().getCheckSection().timer().delaysSize());
		this.negativeTimerEnabled = this.ness().getMainConfig().getCheckSection().timer().negativetimer();
	}

	public interface Config {
		@DefaultDouble(1.1)
		double maxpackets();

		@DefaultInteger(40)
		int delaysSize();

		@DefaultBoolean(false)
		boolean negativetimer();
	}

	/**
	 * Thanks to GladUrBad for a small hint
	 */
	@Override
	protected void checkPacket(Packet packet) {
		NessPlayer nessPlayer = player();
		if (!packet.getRawPacket().getClass().getSimpleName().toLowerCase().contains("position")
				|| nessPlayer.isTeleported() || nessPlayer.isHasSetback()) {
			return;
		}
		final long current = System.nanoTime();
		long result = (long) ((current - lastDelay) / 1e+6);
		if (result > 5) {
			delay.add(result);
		}
		final long average = delay.average();
		final float speed = 50.0f / (float) average;
		if (delay.size() > (this.ness().getMainConfig().getCheckSection().timer().delaysSize() - 1)) {
			if (speed > MAX_PACKETS_PER_TICK) {
				if (++buffer > 7) {
					this.flagEvent(packet, "BasicTimer " + (float) speed);
				}
			} else if ((speed > 0.2 && speed < 0.9) && negativeTimerEnabled) {
				if (++buffer > 6) {
					this.flagEvent(packet, "NegativeTimer " + (float) speed);
				}
			} else if (buffer > 0) {
				buffer -= 0.5;
			}
			nessPlayer.updateTimerTicks(speed);
		}
		this.lastDelay = current;
	}
}

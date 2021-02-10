package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;

public class BalanceTimer extends PacketCheck {
	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	private long lastDelay = System.currentTimeMillis();
	private long balance;
	private long lastBalance;

	public BalanceTimer(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
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
		final long current = System.currentTimeMillis();
		long result = current - lastDelay;
		if (result < 1000) {
			balance += 50;
			balance -= result;
			long resulti = balance - lastBalance;
			if (resulti > 11) {
				nessPlayer.sendDevMessage("Resulti: " + resulti);
			}
			lastBalance = balance;
		}
		this.lastDelay = current;
	}
}
package com.github.ness.check.combat;

import java.time.Duration;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.check.PeriodicTaskInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.Packet;

public class KillauraNoSwing extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos
			.forPacketsWithTask(PeriodicTaskInfo.asyncTask(Duration.ofMillis(200)));

	public KillauraNoSwing(final PacketCheckFactory<?> factory, final NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}

	private long lastAnimation;

	@Override
	protected void checkAsyncPeriodic() {
		// normalPacketsCounter = 0;
		long attackDelay = this.player().milliSecondTimeDifference(PlayerAction.ATTACK);
		long result = Math.abs(attackDelay - lastAnimation);
		if(result > 50) {
			this.flag("Result: " + result);
		}
	}

	@Override
	protected void checkPacket(Packet packet) {
		String packetName = packet.getRawPacket().getClass().getSimpleName().toLowerCase();
		if (packetName.contains("animation")) {
			lastAnimation = (long) (System.nanoTime() / 1e+6);
		}
	}
}

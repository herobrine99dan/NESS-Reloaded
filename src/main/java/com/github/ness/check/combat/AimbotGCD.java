package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.MathUtils;

public class AimbotGCD extends AbstractCheck<ReceivedPacketEvent> {

	public static final CheckInfo<ReceivedPacketEvent> checkInfo = CheckInfo.eventOnly(ReceivedPacketEvent.class);

	private double lastPitch = 0;
	private double lastGCD = 0;
	private int preVL;
	private static final double MULTIPLIER = Math.pow(2.0, 24.0);
	public AimbotGCD(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}
	
	@Override
	protected void checkEvent(ReceivedPacketEvent event) {
		NessPlayer player = event.getNessPlayer();
		if (!player().equals(player)) {
			return;
		}
		if (!event.getPacket().getName().toLowerCase().contains("look") || player.isTeleported()) {
			return;
		}
		float pitch = (float) Math.abs(player.getMovementValues().pitchDiff);
        final double gcd = MathUtils.gcd(16384.0, pitch * MULTIPLIER, lastPitch * MULTIPLIER);
		if (Math.abs(pitch) > 9 || Math.abs(pitch) < 0.05 || pitch == 0.0
				|| Math.abs(player.getMovementValues().getTo().getPitch()) == 90) {
			return;
		}
		if(player.isDevMode()) {
			final double result = Math.abs(gcd - lastGCD);
			if(result > 512 && result < 100000) {
				if(preVL++ > 7) {
					player.setViolation(new Violation("AimbotGCD", " GCD Difference: " + (float) result), event);
				}
			} else {
				if(preVL > 0) {
					preVL--;
				}
			}
		}
		lastPitch = pitch;
		lastGCD = gcd;
	}

}

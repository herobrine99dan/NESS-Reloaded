package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.MathUtils;

public class AimbotGCD extends ListeningCheck<ReceivedPacketEvent> {

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEvent(ReceivedPacketEvent.class);

	private double lastPitch = 0;
	private double lastGCD = 0;
	private int preVL;
	private static final double MULTIPLIER = Math.pow(2.0, 24.0);

	public AimbotGCD(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent event) {
		NessPlayer player = event.getNessPlayer();
		if (!player().equals(player)) {
			return;
		}
		if (!event.getPacket().getName().contains("Look") || player.isTeleported()) {
			return;
		}
		float pitch = (float) Math.abs(player.getMovementValues().pitchDiff);
		final double gcd = MathUtils.getGCD(pitch, lastPitch);
		if (Math.abs(pitch) > 9 || Math.abs(pitch) < 0.05 || pitch == 0.0
				|| Math.abs(player.getMovementValues().getTo().getPitch()) == 90) {
			return;
		}
		final double result = Math.abs(gcd - lastGCD);
		if (result > 512 && result < 100000) {
			if (preVL++ > 6) {
				flag(" Diff: " + result);
				// if(player().setViolation(new Violation("AimbotGCD", "Diff: " + result)))
				// event.setCancelled(true);
			}
		} else {
			if (preVL > 0) {
				preVL--;
			}
		}
		lastPitch = pitch;
		lastGCD = gcd;
	}

}

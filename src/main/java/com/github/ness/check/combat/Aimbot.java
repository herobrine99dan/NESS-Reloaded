package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.List;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.MathUtils;
import com.github.ness.utility.Utility;

public class Aimbot extends ListeningCheck<ReceivedPacketEvent> {

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEvent(ReceivedPacketEvent.class);

	private float lastYaw;
	private float lastPitch;
	private int buffer;

	public Aimbot(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
		lastYaw = 0;
		lastPitch = 0;
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent e) {
		if (!player().equals(e.getNessPlayer())) {
			return;
		}
		if (!e.getPacket().getName().contains("Look") || e.getNessPlayer().isTeleported()) {
			return;
		}
		Check1(e);
		Check2(e);
		Check3(e);
	}

	private void Check1(ReceivedPacketEvent e) {
		NessPlayer np = e.getNessPlayer();
		if (np.getSensitivity() == 0) {
			return;
		}
		if (np.getMovementValues().getYawDiff() < 1) {
			return;
		}
		double firstvar = ((np.getSensitivity() + 1) / 200) * 0.6F + 0.2F;
		float secondvar = (float) (Math.pow(firstvar, 3f));
		double yawResult = np.getMovementValues().getYawDiff() - lastYaw;
		double pitchResult = np.getMovementValues().getPitchDiff() - lastPitch;
		float mouseDeltaX = (float) yawResult / (secondvar * 1.2f);
		float mouseDeltaY = (float) pitchResult / (secondvar * 1.2f);
		// float x = (float) (thirdvar - Math.floor(thirdvar));
		np.sendDevMessage("mouseDeltaX: " + mouseDeltaX + " mouseDeltaY: " + mouseDeltaY);
		lastYaw = (float) np.getMovementValues().getYawDiff();
		lastPitch = (float) np.getMovementValues().getPitchDiff();
	}

	/**
	 * Check for some Aimbot Pattern
	 */
	private void Check2(ReceivedPacketEvent e) {
		NessPlayer player = e.getNessPlayer();
		float yawChange = (float) Math.abs(player.getMovementValues().getYawDiff());
		float pitchChange = (float) Math.abs(player.getMovementValues().getPitchDiff());
		if (yawChange >= 1.0f && yawChange % 0.1f == 0.0f) {
			flag(" PerfectAura");
		} else if (pitchChange >= 1.0f && pitchChange % 0.1f == 0.0f) {
			flag(" PerfectAura1");
		}
	}

	/**
	 * @author Tecnio This check comes from AntiHaxerman
	 *         (https://github.com/Tecnio/AntiHaxerman/blob/master/src/main/java/me/tecnio/antihaxerman/check/impl/aim/AimE.java)
	 * @param e
	 */
	private void Check3(ReceivedPacketEvent e) {
		NessPlayer player = e.getNessPlayer();
		if (player.milliSecondTimeDifference(PlayerAction.ATTACK) < 100) {
			if (player.getMovementValues().getYawDiff() % .25 == 0.0 && player.getMovementValues().getYawDiff() > 0) {
				if (++buffer > 2) {
					flag();
				}
			} else if (buffer > 0) {
				buffer--;
			}
		}
	}
}
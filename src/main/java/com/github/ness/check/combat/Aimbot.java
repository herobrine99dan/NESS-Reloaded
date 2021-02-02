package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;
import com.github.ness.utility.MathUtils;

public class Aimbot extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	private double lastYaw;
	private double lastPitch;
	private int buffer;

	public Aimbot(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		lastYaw = 0;
		lastPitch = 0;
	}

	@Override
	protected void checkPacket(Packet packet) {
		if (!packet.isPacketType(this.packetTypeRegistry().playInFlying()) || player().isTeleported()) {
			return;
		}
		PlayInFlying wrapper = packet.toPacketWrapper(this.packetTypeRegistry().playInFlying());
		if (!wrapper.hasLook()) {
			return;
		}
		Check1(packet);
		Check2(packet);
		Check3(packet);
		Check4(packet);
		lastYaw = this.player().getMovementValues().getYawDiff();
		lastPitch = this.player().getMovementValues().getPitchDiff();
	}

	private void Check1(Packet e) {
		NessPlayer np = player();
		if (np.getSensitivity() == 0) {
			return;
		}
		double firstvar = (np.getSensitivity() / 200) * 0.6F + 0.2F;
		double secondvar = Math.pow(firstvar, 3f);
		double yawResult = this.player().getMovementValues().getYawDiff() - lastYaw;
		double pitchResult = this.player().getMovementValues().getPitchDiff() - lastPitch;
		double mouseDeltaX = yawResult / (secondvar * 1.2f);
		double mouseDeltaY = pitchResult / (secondvar * 1.2f);
		double xError = Math.abs(mouseDeltaX - Math.floor(mouseDeltaX));
		double yError = Math.abs(mouseDeltaY - Math.floor(mouseDeltaY));
		double gcd = MathUtils.gcdRational(Math.abs(this.player().getMovementValues().getPitchDiff()),
				Math.abs(lastPitch));
		// np.sendDevMessage("gcd: " + gcd);
	}

	/**
	 * Check for some Aimbot Pattern
	 */
	private void Check2(Packet e) {
		float yawChange = (float) Math.abs(this.player().getMovementValues().getYawDiff());
		float pitchChange = (float) Math.abs(this.player().getMovementValues().getPitchDiff());
		if (yawChange >= 0.1 && yawChange % 0.1f == 0.0f) {
			flag(" PerfectAura");
		} else if (pitchChange >= 0.1 && pitchChange % 0.1f == 0.0f) {
			flag(" PerfectAura1");
		}
	}

	/**
	 * @author Tecnio This check comes from AntiHaxerman
	 *         (https://github.com/Tecnio/AntiHaxerman/blob/master/src/main/java/me/tecnio/antihaxerman/check/impl/aim/AimE.java)
	 */
	private void Check3(Packet e) {
		NessPlayer player = player();
		if (player.milliSecondTimeDifference(PlayerAction.ATTACK) < 1000) {
			if (this.player().getMovementValues().getYawDiff() % .25 == 0.0
					&& this.player().getMovementValues().getYawDiff() > 0) {
				if (++buffer > 2) {
					flag("PerfectAura3");
				}
			} else if (buffer > 0) {
				buffer--;
			}
		}
	}

	private void Check4(Packet e) {
		double yawDelta = this.player().getMovementValues().getYawDiff();
		double pitchDelta = this.player().getMovementValues().getPitchDiff();
		if (yawDelta > 7 && isReallySmall(pitchDelta)) {
			if (++buffer > 3) {
				this.flag();
			}
		} else if (pitchDelta > 7 && isReallySmall(yawDelta)) {
			if (++buffer > 3) {
				this.flag();
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
	}

	private boolean isReallySmall(double d) {
		return Math.abs(d) < 0.001;
	}
}
package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;

public class Aimbot extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	private double lastYaw;
	private double lastPitch;

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
		// Check1(packet);
		Check2(packet);
		Check3(packet);
		Check4(packet);
		Check5(packet);
		lastYaw = wrapper.yaw();
		lastPitch = wrapper.pitch();
	}

	/*
	 * private void Check1(Packet e) { NessPlayer np = player(); if
	 * (np.getSensitivity() == 0) { return; } double firstvar = (np.getSensitivity()
	 * / 200) * 0.6F + 0.2F; double secondvar = Math.pow(firstvar, 3f); double
	 * yawResult = this.player().getMovementValues().getYawDiff() - lastYaw; double
	 * pitchResult = this.player().getMovementValues().getPitchDiff() - lastPitch;
	 * double mouseDeltaX = yawResult / (secondvar * 1.2f); double mouseDeltaY =
	 * pitchResult / (secondvar * 1.2f); double xError = Math.abs(mouseDeltaX -
	 * Math.floor(mouseDeltaX)); double yError = Math.abs(mouseDeltaY -
	 * Math.floor(mouseDeltaY)); double gcd =
	 * MathUtils.gcdRational(Math.abs(this.player().getMovementValues().getPitchDiff
	 * ()), Math.abs(lastPitch)); // np.sendDevMessage("gcd: " + gcd); }
	 */

	/**
	 * Check for some Aimbot Pattern
	 */
	private void Check2(Packet e) {
		PlayInFlying wrapper = e.toPacketWrapper(this.packetTypeRegistry().playInFlying());
		float yawChange = (float) (wrapper.yaw() % 360 - lastYaw % 360);
		float pitchChange = (float) (wrapper.pitch() - lastPitch);
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
	private double buffer3;

	private void Check3(Packet e) {
		NessPlayer player = player();
		if (player.milliSecondTimeDifference(PlayerAction.ATTACK) < 1000) {
			PlayInFlying wrapper = e.toPacketWrapper(this.packetTypeRegistry().playInFlying());
			float yawChange = (float) (wrapper.yaw() % 360 - lastYaw % 360);
			if (yawChange % .25 == 0.0 && this.player().getMovementValues().getYawDiff() > 0) {
				if (++buffer3 > 2) {
					flag("PerfectAura3");
				}
			} else if (buffer3 > 0) {
				buffer3--;
			}
		}
	}

	private double buffer4;

	private void Check4(Packet e) {
		PlayInFlying wrapper = e.toPacketWrapper(this.packetTypeRegistry().playInFlying());
		float yawDelta = (float) (wrapper.yaw() % 360 - lastYaw % 360);
		float pitchDelta = (float) (wrapper.pitch() - lastPitch);
		if (yawDelta > 30 && isReallySmall(pitchDelta)) {
			if (++buffer4 > 15) {
				this.flag("IrregularYaw");
			}
		} else if (pitchDelta > 30 && isReallySmall(yawDelta)) {
			if (++buffer4 > 8) {
				this.flag("IrregularPitch");
			}
		} else if (buffer4 > 0) {
			buffer4 -= 0.5;
		}
	}

	private void Check5(Packet e) {
		PlayInFlying wrapper = e.toPacketWrapper(this.packetTypeRegistry().playInFlying());
		float yawDeltaPacket = (float) (wrapper.yaw() % 360 - lastYaw % 360);
		float pitchDeltaPacket = (float) (wrapper.pitch() - lastPitch);
		roundedValues(yawDeltaPacket, pitchDeltaPacket, wrapper.yaw(), wrapper.pitch());
		commonPattern(yawDeltaPacket, pitchDeltaPacket, wrapper.yaw(), wrapper.pitch());
	}
	
	private double lastPitchDelta;
	private double buffer6;

	private double buffer5;

	private void roundedValues(float yawDeltaPacket, float pitchDeltaPacket, float yaw, float pitch) {
		if (Math.abs(yawDeltaPacket) == Math.round(Math.abs(yawDeltaPacket)) && Math.abs(yawDeltaPacket) > 0.1) {
			if (++buffer5 > 2) {
				this.flag("PerfectRotation1");
			}
		} else if (Math.abs(pitchDeltaPacket) == Math.round(Math.abs(pitchDeltaPacket))
				&& Math.abs(pitchDeltaPacket) > 0.1 && Math.abs(pitch) < 89) {
			if (++buffer5 > 2) {
				this.flag("PerfectRotation2");
			}
		} else if (Math.abs(yaw) == Math.round(Math.abs(yaw)) && Math.abs(yawDeltaPacket) > 1f) {
			if (++buffer5 > 5) {
				this.flag("PerfectRotation3");
			}
		} else if (Math.abs(pitch) == Math.round(Math.abs(pitch)) && Math.abs(pitchDeltaPacket) > 1f && Math.abs(pitch) < 89) {
			if (++buffer5 > 3) {
				this.flag("PerfectRotation4");
			}
		} else if (buffer5 > 0) {
			buffer5 -= 0.5;
		}
	}

	private void commonPattern(float yawDeltaPacket, float pitchDeltaPacket, float yaw, float pitch) {
		if (Math.abs(yaw) % 0.5D == 0.0D) {
			if (++buffer5 > 3) {
				this.flag("PerfectRotation4");
			}
		} else if (Math.abs(yawDeltaPacket) % 0.5D == 0.0D) {
			if (++buffer5 > 3) {
				this.flag("PerfectRotation4");
			}
		}
	}

	private boolean isReallySmall(double d) {
		return Math.abs(d) < 0.001;
	}
}
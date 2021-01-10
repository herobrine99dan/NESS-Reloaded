package com.github.ness.check.required;

import java.awt.geom.Point2D;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;

public class CinematicProcessor extends PacketCheck {
	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	public CinematicProcessor(PacketCheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}

	private Point2D.Float lastRotation;

	@Override
	protected void checkPacket(Packet packet) {
		if (!packet.isPacketType(packetTypeRegistry().playInFlying())) {
			return;
		}
		PlayInFlying wrapper = packet.toPacketWrapper(packetTypeRegistry().playInFlying());
		if (wrapper.hasLook()) {
			Point2D.Float rotation = new Point2D.Float(wrapper.yaw(), wrapper.pitch());
			process(rotation);
			lastRotation = rotation;
		}
	}

	private double lastDeltaYaw;
	private double lastDeltaPitch;
	private double lastPitchAccel;
	private double lastYawAccel;
	private int cinematicTicks;
	private long lastCinematic;

	private boolean isReallySmall(double d) {
		return d < .0001 && d > 0;
	}

	private long ticks() {
		return (long) (System.nanoTime() / 1e+6);
	}

	private void process(Point2D.Float currentRotation) {
		double deltaYaw = currentRotation.getX() - lastRotation.getX();
		double deltaPitch = currentRotation.getY() - lastRotation.getY();
		double yawAccel = Math.abs(deltaYaw - lastDeltaYaw);
		double pitchAccel = Math.abs(deltaPitch - lastDeltaPitch);
		double yawAccelAccel = Math.abs(yawAccel - lastYawAccel);
		double pitchAccelAccel = Math.abs(pitchAccel - lastPitchAccel);
		final boolean invalidYaw = yawAccelAccel < 0.05 && yawAccelAccel > 0;
		final boolean invalidPitch = pitchAccelAccel < 0.05 && pitchAccelAccel > 0;
		final boolean exponentialYaw = isReallySmall(yawAccelAccel);
		final boolean exponentialPitch = isReallySmall(pitchAccelAccel);
		if (player().getSensitivity() < 100 && (exponentialYaw || exponentialPitch)) {
			cinematicTicks += 3.5;
		} else if (invalidYaw || invalidPitch) {
			cinematicTicks += 1.75;
		} else if (cinematicTicks > 0) {
			cinematicTicks -= 0.6;
		}
		if (cinematicTicks > 20) {
			cinematicTicks -= 1.5;
		}
		boolean cinematic = cinematicTicks > 7.5 || (ticks() - lastCinematic < 120);
		if (cinematic && cinematicTicks > 7.5) {
			lastCinematic = ticks();
		}
		lastDeltaYaw = deltaYaw;
		lastDeltaPitch = deltaPitch;
		lastYawAccel = yawAccel;
		lastPitchAccel = pitchAccel;
	}
}

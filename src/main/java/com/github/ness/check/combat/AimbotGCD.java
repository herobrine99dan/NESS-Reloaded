package com.github.ness.check.combat;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;
import com.github.ness.utility.MathUtils;

public class AimbotGCD extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();
	private Point2D.Float lastRotation = new Point2D.Float(0, 0);
	private double lastPitch;
	private double lastYaw;
	private double buffer;

	public AimbotGCD(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

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

	private void process(Point2D.Float rotation) {
		NessPlayer player = player();
		double yawDelta = Math.abs(rotation.getX() - lastRotation.getX());
		double pitchDelta = Math.abs(rotation.getY() - lastRotation.getY());
		if (Math.abs(pitchDelta) >= 10 || pitchDelta == 0.0 || player.isTeleported() || player.isHasSetback()
				|| Math.abs(rotation.getY()) == 90 || player.isCinematic()) {
			return;
		}
		double gcdYaw = MathUtils.gcdRational(yawDelta, lastYaw);
		double gcdPitch = MathUtils.gcdRational(pitchDelta, lastPitch);
		//this.player().sendDevMessage("gcdYaw: " + (float) gcdYaw + " gcdPitch: " + (float) gcdPitch);
		if (gcdYaw < 0.005 || gcdPitch < 0.005) {
			if (++buffer > 30) {
				this.flag();
			}
		} else if (buffer > 0) {
			buffer--;
		}
		lastPitch = pitchDelta;
		lastYaw = yawDelta;
	}
}

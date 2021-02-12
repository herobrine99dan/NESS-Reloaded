package com.github.ness.check.required;

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

public class SensitivityUpdater extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();
	private List<Double> pitchDiff = new ArrayList<Double>();
	private double lastGCD = 0;
	private Point2D.Float lastRotation = new Point2D.Float(0, 0);

	public SensitivityUpdater(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.pitchDiff = new ArrayList<Double>();
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
		double pitchDelta = Math.abs(rotation.getY() - lastRotation.getY());
		if (Math.abs(pitchDelta) >= 10 || Math.abs(pitchDelta) < 0.05 || pitchDelta == 0.0 || player.isTeleported()
				|| player.isHasSetback() || Math.abs(rotation.getY()) == 90) {
			return;
		}
		pitchDiff.add(pitchDelta);
		if (pitchDiff.size() >= 15) {
			final float gcd = (float) MathUtils.gcdRational(pitchDiff);
			if (lastGCD == 0.0) {
				lastGCD = gcd;
			}
			double result = Math.abs(gcd - lastGCD);
			final int sensitivity = (int) Math.round(MathUtils.getSensitivity(gcd) * 200);
			if (player.isCinematic()) {
				pitchDiff.clear();
				return;
			}
			if (result < 0.01 && gcd > 0.05) {
				player.setGcd(gcd);
				player.setSensitivity(sensitivity);
			}
			pitchDiff.clear();
			lastGCD = gcd;
		}
	}

}

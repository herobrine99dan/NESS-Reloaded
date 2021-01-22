package com.github.ness.check.required;

import java.awt.geom.Point2D;
import java.util.List;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;
import com.github.ness.utility.GraphUtil;
import com.google.common.collect.Lists;

public class FrequencyCinematicProcessor extends PacketCheck {
	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	public FrequencyCinematicProcessor(PacketCheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}

	private Point2D.Float lastRotation = new Point2D.Float(0, 0);

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

	/**
	 * From Frequency
	 * https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/check/impl/aimassist/cinematic/Cinematic.java
	 * 
	 * @author Elevated
	 *
	 */

	private long lastSmooth = 0L, lastHighRate = 0L;
	private double lastDeltaYaw = 0.0d, lastDeltaPitch = 0.0d;

	private final List<Double> yawSamples = Lists.newArrayList();
	private final List<Double> pitchSamples = Lists.newArrayList();

	private void process(Point2D.Float currentRotation) {
		final long now = System.currentTimeMillis();

		final double deltaYaw = Math.abs(currentRotation.getX() - lastRotation.getX());
		final double deltaPitch = Math.abs(currentRotation.getY() - lastRotation.getY());
		final double differenceYaw = Math.abs(deltaYaw - lastDeltaYaw);
		final double differencePitch = Math.abs(deltaPitch - lastDeltaPitch);

		final double joltYaw = Math.abs(differenceYaw - deltaYaw);
		final double joltPitch = Math.abs(differencePitch - deltaPitch);
		final double lastHighRateResult = now - lastHighRate;
		final double lastSmoothResult = now - lastSmooth;
		final boolean cinematic = lastHighRateResult > 250L || lastSmoothResult < 4000L;

		if (joltYaw > 1.0 && joltPitch > 1.0) {
			this.lastHighRate = now;
		}

		if (deltaPitch > 0.0 && deltaPitch > 0.0) {
			yawSamples.add(deltaYaw);
			pitchSamples.add(deltaPitch);
		}

		if (yawSamples.size() == 20 && pitchSamples.size() == 20) {
			// Get the cerberus/positive graph of the sample-lists
			final GraphUtil.GraphResult resultsYaw = GraphUtil.getGraph(yawSamples);
			final GraphUtil.GraphResult resultsPitch = GraphUtil.getGraph(pitchSamples);
			// Negative values
			final int negativesYaw = resultsYaw.getNegatives();
			final int negativesPitch = resultsPitch.getNegatives();
			// Positive values
			final int positivesYaw = resultsYaw.getPositives();
			final int positivesPitch = resultsPitch.getPositives();
			// Cinematic camera usually does this on *most* speeds and is accurate for the
			// most part.
			if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) {
				this.lastSmooth = now;
			}
			yawSamples.clear();
			pitchSamples.clear();
		}
		player().setCinematic(cinematic);
		this.lastDeltaYaw = deltaYaw;
		this.lastDeltaPitch = deltaPitch;
	}
}

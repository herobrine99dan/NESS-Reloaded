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
	private static final double MODULO_THRESHOLD = 90F;
	private static final double LINEAR_THRESHOLD = 0.1F;
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
			processCheck(rotation);
			lastRotation = rotation;
		}
	}

	/**
	 * (I love Frequency!)
	 * From https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/check/impl/aimassist/AimAssistE.java
	 * @author Elevated
	 */

	private void processCheck(Point2D.Float rotation) {
        final float deltaYaw = (float) Math.abs(rotation.getX() - lastRotation.getX());
        final float deltaPitch = (float) Math.abs(rotation.getY() - lastRotation.getY());

        // Grab the gcd using an expander.
        final double divisorYaw = MathUtils.gcdRational(deltaYaw, lastYaw);
        final double divisorPitch = MathUtils.gcdRational(deltaPitch, lastPitch);

        // Get the estimated mouse delta from the constant
        final double currentX = deltaYaw / divisorYaw;
        final double currentY = deltaPitch / divisorPitch;

        // Get the estimated mouse delta from the old rotations using the new constant
        final double previousX = lastYaw / divisorYaw;
        final double previousY = lastPitch / divisorPitch;
        NessPlayer nessPlayer = player();
        if(nessPlayer.isCinematic()) {
        	if(buffer > 0) {
        		buffer--;
        	}
        	return;
        }
        // Make sure the rotation is not very large and not equal to zero and get the modulo of the xys
        if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f) {
            final double moduloX = currentX % previousX;
            final double moduloY = currentY % previousY;
            // Get the floor delta of the the modulos
            final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
            final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

            // Impossible to have a different constant in two rotations
            final boolean invalidX = moduloX > MODULO_THRESHOLD && floorModuloX > LINEAR_THRESHOLD;
            final boolean invalidY = moduloY > MODULO_THRESHOLD && floorModuloY > LINEAR_THRESHOLD;

            if (invalidX && invalidY) {
                if (++buffer > 4) flag("Buffer: " + buffer);
            } else if(buffer > 0) {
                buffer -= 0.5;
            }
        }
		lastPitch = deltaPitch;
		lastYaw = deltaYaw;
	}

	private void process(Point2D.Float rotation) {
		NessPlayer player = player();
		double yawDelta = Math.abs(rotation.getX() - lastRotation.getX());
		double pitchDelta = Math.abs(rotation.getY() - lastRotation.getY());
		if (player.isTeleported() || player.isHasSetback() || Math.abs(rotation.getY()) == 90 || player.isCinematic()) {
			if (buffer > 0) {
				buffer--;
			}
			return;
		}
		double gcdYaw = MathUtils.euclideanGCD(yawDelta, lastYaw);
		double gcdPitch = MathUtils.euclideanGCD(pitchDelta, lastPitch);
		// this.player()
		// .sendDevMessage("buffer: " + buffer + " gcdYaw: " + (float) gcdYaw + "
		// gcdPitch: " + (float) gcdPitch);
		if (gcdYaw < 0.005 || gcdPitch < 0.005) {
			if (++buffer > 25) {
				this.flag();
			}
		} else if (buffer > 0) {
			buffer--;
		}
		lastPitch = pitchDelta;
		lastYaw = yawDelta;
	}
}

package com.github.ness.check.movement;

import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.ReceivedPacketEvent;

public class OmniSprint extends AbstractCheck<ReceivedPacketEvent> {

	double maxXZDiff;
	double maxYDiff;

	public static final CheckInfo<ReceivedPacketEvent> checkInfo = CheckInfo.eventOnly(ReceivedPacketEvent.class);

	public OmniSprint(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.maxYDiff = this.ness().getNessConfig().getCheck(this.getClass()).getDouble("maxxzdiff", 1.5);
		this.maxXZDiff = this.ness().getNessConfig().getCheck(this.getClass()).getDouble("maxydiff", 1);
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent event) {
		NessPlayer nessPlayer = event.getNessPlayer();
		// Vector result =
		// event.getTo().toVector().subtract(event.getFrom().toVector());
		MovementValues values = nessPlayer.getMovementValues();
		if (nessPlayer.getMovementValues().isSprinting) {
			if (values.serverVelocity.getY() > 0.0 || nessPlayer.getMovementValues().yawDiff > 10) {
				return;
			}
			Vector moving = values.getFrom().toBukkitLocation().clone()
					.subtract(values.getTo().toBukkitLocation().clone()).toVector();
			double angle = moving.angle(getDirection(values.getTo()));
			if (angle < 1.58) {
				if(player().setViolation(new Violation("Sprint", "BadDirection"))) event.setCancelled(true);
			}
		}
	}

	private Vector getDirection(ImmutableLoc loc) {
		Vector vector = new Vector();
		double rotX = loc.getYaw();
		double rotY = 3;
		vector.setY(-Math.sin(Math.toRadians(rotY)));
		double xz = Math.cos(Math.toRadians(rotY));
		vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
		vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
		return vector;
	}

}

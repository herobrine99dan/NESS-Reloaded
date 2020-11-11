package com.github.ness.check.movement;

import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;

public class OmniSprint extends ListeningCheck<ReceivedPacketEvent> {

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos.forEvent(ReceivedPacketEvent.class);

	public OmniSprint(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent event) {
		NessPlayer nessPlayer = event.getNessPlayer();
		// Vector result =
		// event.getTo().toVector().subtract(event.getFrom().toVector());
		MovementValues values = nessPlayer.getMovementValues();
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.SPRINT) < 100) {
			if (values.getServerVelocity().getY() > 0.0 || nessPlayer.getMovementValues().yawDiff > 10) {
				return;
			}
			Vector moving = values.getFrom().toBukkitLocation().clone()
					.subtract(values.getTo().toBukkitLocation().clone()).toVector();
			double angle = moving.angle(getDirection(values.getTo()));
			if (angle < 1.58) {
				flagEvent(event);
				//if(player().setViolation(new Violation("OmniSprint", ""))) event.setCancelled(true);
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

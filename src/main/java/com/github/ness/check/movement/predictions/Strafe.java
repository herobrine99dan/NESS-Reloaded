package com.github.ness.check.movement.predictions;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

public class Strafe extends ListeningCheck<PlayerMoveEvent> {

    public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

    public Strafe(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
        super(factory, player);
    }

    private double buffer;

    @Override
    protected void checkEvent(PlayerMoveEvent e) {
        if (this.player().isUsingGeyserMC()) {
            return;
        }
        MovementValues movementValues = this.player().getMovementValues();
        float friction = getCorrectFriction();
        final double xzDiff = movementValues.getXZDiff();
        if (e.getPlayer().isInsideVehicle()) {
            return;
        }
        Vector moving = e.getFrom().toVector().subtract(e.getTo().toVector());
        moving.setY(0);
        moving.divide(new Vector(friction, 1, friction));

        Vector direction = getDirectionOfOnlyYaw(movementValues.getTo().getYaw());
        float angle = (float) Math.toDegrees(moving.angle(direction));
        float subtraction = Math.abs(Math.round(angle) - angle);
        if (this.player().milliSecondTimeDifference(PlayerAction.JOIN) > 10000) {
            if (subtraction < 0.001 && xzDiff > 0.15 && ++buffer > 1) {
                this.flag("Strafe: " + subtraction);
            } else if (angle < 0.001 && xzDiff > 0.15 && ++buffer > 1) {
                this.flag("IrregularStrafeAngle");
            } else if (buffer > 0) {
                buffer -= 0.5;
            }
        }
    }

    private float getCorrectFriction() {
        return 0.91f; // Only for now
    }

    private Vector getDirectionOfOnlyYaw(double yaw) {
        Vector vector = new Vector();
        double rotX = Math.toRadians(yaw);
        vector.setY(0);// vector.setY(-Math.sin(Math.toRadians(rotY))); sin(0)=0
        double xz = 1.0;// double xz = Math.cos(Math.toRadians(rotY)); cos(0)=1
        vector.setX(-xz * Math.sin(rotX));
        vector.setZ(xz * Math.cos(rotX));

        return vector;
    }
}

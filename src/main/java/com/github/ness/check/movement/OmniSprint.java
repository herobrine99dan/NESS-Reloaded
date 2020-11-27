package com.github.ness.check.movement;

import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.MutableVector;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.Utility;

public class OmniSprint extends Check {

    public OmniSprint(NessPlayer player) {
        super(OmniSprint.class, player);
    }

    private MutableVector getDirection(ImmutableLoc loc) {
        MutableVector vector = new MutableVector();
        double rotX = loc.getYaw();
        double rotY = 3;
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }

    @Override
    public void onFlying(FlyingEvent e) {
        NessPlayer nessPlayer = e.getNessPlayer();
        MovementValues values = nessPlayer.getMovementValues();
        if (values.isSprinting()) {
            if (values.getServerVelocity().getY() > 0.0 || nessPlayer.getMovementValues().yawDiff > 10) {
                return;
            }
            //TODO Do Debug on Dot Value and Angle value
            final MutableVector direction = this.getDirection(values.getTo());
            MutableVector moving = new MutableVector(values.getFrom().toBukkitLocation().clone()
                    .subtract(values.getTo().toBukkitLocation().clone()).toVector());
            double angle = moving.angle(direction);
            MutableVector toEntity = new MutableVector(values.getTo().toBukkitLocation().toVector()
                    .subtract(values.getFrom().toBukkitLocation().toVector()));
            double dot = toEntity.normalize().dot(direction);
            nessPlayer.sendDevMessage("Dot: " + (float) dot + " Angle: " + (float) angle + " Block: " + values.getBlockUnder().getType());
            if (angle < 1.58) {
                flag(e);
                // if(player().setViolation(new Violation("OmniSprint", "")))
                // event.setCancelled(true);
            }
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }

}

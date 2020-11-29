package com.github.ness.check.movement.fly;

import org.bukkit.Bukkit;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.Utility;

public class FlyFalseGround extends Check {

    private int preVL;

    public FlyFalseGround(NessPlayer player, CheckManager manager) {
        super(FlyFalseGround.class, player, manager);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.isPosition()) {
            return;
        }
        NessPlayer nessPlayer = this.player();
        MovementValues movementValues = nessPlayer.getMovementValues();
        if (Bukkit.getVersion().contains("1.8") || movementValues.isAroundLily() || movementValues.isAroundCarpet()
                || movementValues.isAroundSnow() || movementValues.isAroundScaffolding()) {
            return;
        }
        if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500
                && nessPlayer.getLastVelocity().getY() > 0.35) {
            return;
        }
        if (!nessPlayer.isTeleported() && movementValues.isEntityAround() && !movementValues.isFlyBypass()
                && !movementValues.isAroundSlime() && !movementValues.isInsideVehicle()
                && !player().getMovementValues().isAroundWeb()) {
            if (e.isOnGround() && !movementValues.isGroundAround() && !movementValues.isAroundLadders()) {
                flag(" FalseGround", e);
                // if(player().setViolation(new Violation("Fly", "FalseGround")))
                // e.setCancelled(true);
            } else if (e.isOnGround() && !Utility.isMathematicallyOnGround(movementValues.getTo().getY())) {
                if (++preVL > 2) {
                    flag(" FalseGround1", e);
                }
                // if(player().setViolation(new Violation("Fly", "FalseGround1")))
                // e.setCancelled(true);
            } else if (preVL > 0) {
                preVL--;
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
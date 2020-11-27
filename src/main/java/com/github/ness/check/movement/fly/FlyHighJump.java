package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.ReflectionUtility;
import com.github.ness.utility.Utility;

public class FlyHighJump extends Check {

    float flyYSum;

    public FlyHighJump(NessPlayer player) {
        super(FlyHighJump.class, player);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if(!e.isPosition()) {
            return;
        }
        NessPlayer nessPlayer = this.player();
        final MovementValues movementValues = nessPlayer.getMovementValues();
        double y = movementValues.yDiff;
        if (Utility.isMathematicallyOnGround(movementValues.getTo().getY()) || movementValues.isFlyBypass()
                || movementValues.isAroundSlime() || movementValues.isAbleFly() || movementValues.isAroundLiquids()
                || movementValues.isAroundLily() || movementValues.isAroundSea() || movementValues.isAroundSlabs()
                || movementValues.isAroundStairs() || movementValues.isAroundLiquids()
                || ReflectionUtility.getBlockName(p, ImmutableLoc.of(e.getTo().clone().add(0, -0.5, 0)))
                        .contains("scaffolding")
                || ReflectionUtility.getBlockName(p, ImmutableLoc.of(e.getTo().clone().add(0, 0.5, 0)))
                        .contains("scaffolding")
                || movementValues.isAroundSnow() || movementValues.isAroundLadders() || nessPlayer.isTeleported()
                || movementValues.isInsideVehicle() || movementValues.isThereVehicleNear()
                || nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKPLACED) < 1000) {
            flyYSum = 0;
            return;
        }
        if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500) {
            y -= Math.abs(nessPlayer.getLastVelocity().getY());
        }
        if (y > 0) {
            flyYSum += y;
            double max = 1.30;
            int jumpBoost = movementValues.getJumpPotion();
            max += jumpBoost * (max / 2);
            if (flyYSum > max && movementValues.getServerVelocity().getY() < 0) {
                flag(" ySum: " + flyYSum, e);
                // if(player().setViolation(new Violation("Fly", "HighJump ySum: " + flyYSum)))
                // e.setCancelled(true);
            }
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {}

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) { }

}
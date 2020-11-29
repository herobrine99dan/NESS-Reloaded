package com.github.ness.check.combat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;

public class KillauraKeepSprint extends Check {
    private double lastDeltaXZ;
    private int bufferViolation;

    public KillauraKeepSprint(NessPlayer nessPlayer, CheckManager manager) {
        super(Aimbot.class, nessPlayer, manager);
        lastDeltaXZ = 0;
    }
    
    /**
     * Created on 10/24/2020 Package me.frep.vulcan.check.impl.movement.aim by frep
     * (https://github.com/freppp/)
     */
    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.getPacket().isPosition() || e.getNessPlayer().isTeleported()) {
            return;
        }
        if (e.getNessPlayer().getLastEntityAttacked() == null) {
            return;
        }
        NessPlayer player = e.getNessPlayer();
        MovementValues values = player.getMovementValues();
        final double deltaXZ = values.getXZDiff();

        final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);

        final long swingDelay = player.milliSecondTimeDifference(PlayerAction.ATTACK);

        final boolean sprinting = values.isSprinting();

        final boolean validTarget = Bukkit.getEntity(player.getLastEntityAttacked()) instanceof Player;
        final boolean invalid = acceleration < .0025 && sprinting && deltaXZ > .22 && swingDelay < 150 && validTarget;
        if (invalid) {
            if (++bufferViolation > 4) {
                this.flag(e);
            }
        } else if (bufferViolation > 0) {
            bufferViolation--;
        }
        this.lastDeltaXZ = deltaXZ;
    }
}

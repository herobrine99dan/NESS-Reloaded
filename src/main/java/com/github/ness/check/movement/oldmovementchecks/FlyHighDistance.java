package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class FlyHighDistance extends Check {

    int preVL;

    public FlyHighDistance(NessPlayer nessPlayer, CheckManager manager) {
        super(FlyHighDistance.class, nessPlayer, manager);
    }

    @Override
    public void checkEvent(Event ev) {
        System.out.println("Checking Instance");
        if (ev instanceof PlayerMoveEvent) {
            System.out.println("Checked instance");
            PlayerMoveEvent e = (PlayerMoveEvent) ev;
            System.out.println("Converted Instance");
            MovementValues values = player().getMovementValues();
            Player player = e.getPlayer();
            double dist = values.getXZDiff();
            if (Utility.hasflybypass(player) || player.getAllowFlight() || Utility.hasVehicleNear(player, 4)
                    || player().isTeleported()) {
                System.out.println("Blocked Check");
                return;
            }
            if (player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 1600) {
                dist -= Math.abs(player().getLastVelocity().getX()) + Math.abs(player().getLastVelocity().getZ());
            }
            System.out.println("Here");
            if (!values.isGroundAround() && dist > 0.35 && values.yDiff == 0.0
                    && this.player().getTimeSinceLastWasOnIce() >= 1000) {
                System.out.println("Done");
                if (preVL++ > 1) {
                    this.flag("Cheats");
                }
            } else if (preVL > 0) {
                preVL--;
            }
        }
    }

}

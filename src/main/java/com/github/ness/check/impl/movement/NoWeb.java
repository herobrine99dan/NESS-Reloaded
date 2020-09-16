package com.github.ness.check.impl.movement;

import com.github.ness.check.CheckManager;
import com.github.ness.NESSPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class NoWeb extends AbstractCheck<PlayerMoveEvent> {

    public NoWeb(CheckManager manager) {
        super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
    }

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        NESSPlayer nessPlayer = this.manager.getPlayer(p);
        float dist = (float) nessPlayer.getMovementValues().XZDiff; // Our XZ Distance
        final double walkSpeed = p.getWalkSpeed() * 0.85;
        dist -= (dist / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
        if (nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) < 1300) {
            dist -= Math.abs(nessPlayer.velocity.getX()) + Math.abs(nessPlayer.velocity.getZ());
        }
        if (dist > walkSpeed && Utility.getMaterialName(event.getTo()).contains("web")
                && Utility.getMaterialName(event.getFrom()).contains("web") && !Utility.hasflybypass(p) && nessPlayer.nanoTimeDifference(PlayerAction.WEBBREAKED) > 1300) {
            nessPlayer.setViolation(new Violation("NoWeb", "Dist: " + dist), event);
        }
    }

}

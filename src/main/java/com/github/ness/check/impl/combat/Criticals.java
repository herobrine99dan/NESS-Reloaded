package com.github.ness.check.impl.combat;

import com.github.ness.check.CheckManager;
import com.github.ness.NESSPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Criticals extends AbstractCheck<EntityDamageByEntityEvent> {

    public Criticals(CheckManager manager) {
        super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void checkEvent(EntityDamageByEntityEvent e) {
        Check(e);
        // newCheck(e);
    }

    public void Check(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (!player.isOnGround() && player.getFallDistance() > 0 && !Utility.hasflybypass(player)
                    && !player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
                    && !player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid()) {
                NESSPlayer np = manager.getPlayer(player);
                if (np.getMovementValues().getTo().getY() % 1.0D == 0.0D
                        && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                    np.setViolation(new Violation("Criticals", ""), event);
                }
            }
        }
    }

}

package com.github.ness.check.combat;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class Criticals extends AbstractCheck<EntityDamageByEntityEvent> {

	public static final CheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfo
			.eventOnly(EntityDamageByEntityEvent.class);

	public Criticals(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(EntityDamageByEntityEvent e) {
		if (player().isNot(e.getDamager()))
			return;
		check(e);
	}

	private void check(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (!player.isOnGround() && player.getFallDistance() > 0 && !Utility.hasflybypass(player)
					&& !player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
					&& !player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid()) {
				NessPlayer np = player();
				if (np.getMovementValues().getTo().getY() % 1.0D == 0.0D
						&& player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
					np.setViolation(new Violation("Criticals", ""), event);
				}
			}
		}
	}

}

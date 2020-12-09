package com.github.ness.check.combat;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.Utility;

public class Criticals extends ListeningCheck<EntityDamageByEntityEvent> {

	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEvent(EntityDamageByEntityEvent.class);

	public Criticals(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
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
			MovementValues values = player().getMovementValues();
			if (!player.isOnGround() && player.getFallDistance() > 0 && !Utility.hasflybypass(player)
					&& !player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
					&& !player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid()
					&& !Utility.hasVehicleNear(player, 3)
					&& !Utility.getMaterialName(values.getTo().toBukkitLocation()).contains("WEB")
					&& !Utility.getMaterialName(values.getFrom().toBukkitLocation()).contains("WEB")) {
				NessPlayer np = player();
				if (np.getMovementValues().getTo().getY() % 1.0D == 0.0D
						&& player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
					flagEvent(event);
					// if(player().setViolation(new Violation("Criticals", "")))
					// event.setCancelled(true);
				}
			}
		}
	}

}

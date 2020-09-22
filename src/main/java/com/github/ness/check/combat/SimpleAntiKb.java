package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SimpleAntiKb extends AbstractCheck<EntityDamageByEntityEvent> {

	public static final CheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfo
			.eventOnly(EntityDamageByEntityEvent.class);

	public SimpleAntiKb(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(EntityDamageByEntityEvent event) {
		if (player().isNot(event.getEntity())) {
			return;
		}
		Player player = (Player) event.getEntity();
		checkPlayerMovesABit(player);
	}

	// Ensure Player moves in response to damage event
	private void checkPlayerMovesABit(Player player) {

		final Location from = player.getLocation();
		if (Utility.isClimbableBlock(from.getBlock()) || Utility.specificBlockNear(from.clone(), "web")
				|| Utility.hasKbBypass(player)) {
			return;
		}
		runTaskLater(() -> {
			Location to = player.getLocation();
			if (to.distanceSquared(from) < .4 && !player.getLocation().add(0, 2, 0).getBlock().getType().isSolid()
					&& !player.getLocation().getBlock().getType().isSolid()) {
				player().setViolation(new Violation("AntiKb", ""), null);
			}
		}, durationOfTicks(5));
	}
}

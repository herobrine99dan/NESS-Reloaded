package com.github.ness.check.combat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;

public class AntiKb extends ListeningCheck<EntityDamageByEntityEvent> {

	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEvent(EntityDamageByEntityEvent.class);

	public AntiKb(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
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
			if (to.distanceSquared(from) < 0.3 && !player.getLocation().add(0, 2, 0).getBlock().getType().isSolid()
					&& !player.getLocation().getBlock().getType().isSolid()) {
				flag();
				//player().setViolation(new Violation("AntiKb", ""));
			}
		}, durationOfTicks(5));
	}
}

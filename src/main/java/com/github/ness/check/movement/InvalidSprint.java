package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class InvalidSprint extends ListeningCheck<PlayerMoveEvent> {

	double maxXZDiff;
	double maxYDiff;

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	protected InvalidSprint(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}
	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.isSprinting()) {
			if (player.hasPotionEffect(PotionEffectType.BLINDNESS) || player.getFoodLevel() < 7) {
				flag();
				//if(player().setViolation(new Violation("Sprint", "ImpossibleActions"))) event.setCancelled(true);
			}
		}
	}
}

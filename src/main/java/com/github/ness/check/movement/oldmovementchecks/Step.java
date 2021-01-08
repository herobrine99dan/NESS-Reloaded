package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.Utility;

public class Step extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public Step(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}
	
	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		MovementValues values = player().getMovementValues();
		if (values.getHelper().hasflybypass(player) || values.isAbleFly() || values.getHelper().isVehicleNear()
				|| player().isTeleported()) {
			return;
		}
		double jumpBoost = Utility.getPotionEffectLevel(player, PotionEffectType.JUMP);
		double yDiffUpper = values.getyDiff();
		yDiffUpper -= jumpBoost * 0.1;
		if (yDiffUpper > 0.6 && values.isGroundAround() && !values.isAroundSlime()) {
			if (player.getVelocity().getY() < 0.43) {
				flagEvent(e, "High Distance: " + yDiffUpper);
			}
		} else if (yDiffUpper < -1.5 && player.getFallDistance() == 0.0 && player.getVelocity().getY() < 0.43) {
			flagEvent(e, "LowDistance");
		}
	}

}

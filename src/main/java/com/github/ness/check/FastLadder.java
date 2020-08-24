package com.github.ness.check;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.utility.Utility;

public class FastLadder extends AbstractCheck<PlayerMoveEvent> {

	protected HashMap<String, Integer> noground = new HashMap<String, Integer>();

	public FastLadder(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		if (Utility.isClimbableBlock(p.getLocation().getBlock()) && !p.hasPotionEffect(PotionEffectType.JUMP)
				&& !Utility.hasflybypass(p) || !this.manager.getPlayer(p).isTeleported()) {
			double distance = np.getMovementValues().yDiff;
			if (distance > 0.155D && p.getVelocity().getY() < 0) {
				punish(event, p, "FastLadder: " + (float) distance);
			}
		}
	}
}

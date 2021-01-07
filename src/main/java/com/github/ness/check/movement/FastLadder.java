package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class FastLadder extends ListeningCheck<PlayerMoveEvent> {

	double maxDist;
	int jumped = 0;
	double buffer;

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FastLadder(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxDist = this.ness().getMainConfig().getCheckSection().fastLadder().maxDist();
	}

	public interface Config {
		@DefaultDouble(0.145)
		double maxDist();
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		if ((float) event.getTo().getY() == 0.42F) {
			jumped++;
		}
		final String name = event.getTo().getBlock().getType().name();
		if ((name.contains("LADDER") || name.contains("VINE")) && !p.hasPotionEffect(PotionEffectType.JUMP)
				&& !nessPlayer.getMovementValues().getHelper().hasflybypass(p) && !nessPlayer.isTeleported() && !nessPlayer.isHasSetback()) {
			double distance = nessPlayer.getMovementValues().getyDiff();
			if (distance > maxDist && p.getVelocity().getY() < 0) {
				if (++buffer > 4) {
					flagEvent(event, "HighDistance Dist: " + (float) distance);
				}
			} else if (distance < -0.192D && p.getVelocity().getY() < 0) {
				if(++buffer > 2) {
					flagEvent(event, "LowDistance Dist: " + (float) distance);
				}
			} else if (buffer > 0) {
				buffer -= .25;
			}
		}
	}
}

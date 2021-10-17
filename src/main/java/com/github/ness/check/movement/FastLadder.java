package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class FastLadder extends ListeningCheck<PlayerMoveEvent> {

	double maxDist;
	double buffer;

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FastLadder(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxDist = this.ness().getMainConfig().getCheckSection().fastLadder().maxDist();
	}

	public interface Config {
		@DefaultDouble(0.13)
		double maxDist();
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		final String name = event.getTo().getBlock().getType().name();
		if ((name.contains("LADDER") || name.contains("VINE"))
				&& !nessPlayer.getMovementValues().getHelper().hasflybypass(nessPlayer) && !nessPlayer.isTeleported()
				&& !nessPlayer.isHasSetback()) {
			double distance = nessPlayer.getMovementValues().getyDiff();
			double maxDist = this.maxDist;
			double maxLowDist = -0.192;
			if(values.hasBubblesColumns() == 1) {
				maxDist = 0.57;
			}
			if(values.hasBubblesColumns() == -1) {
				maxLowDist = -0.57;
			}
			if (distance > maxDist && player.getVelocity().getY() < 0) {
				if (++buffer > 2) {
					flag("HighDistance Dist: " + (float) distance);
				}
			} else if (distance < maxLowDist && player.getVelocity().getY() < 0) {
				if (++buffer > 2) {
					flag("LowDistance Dist: " + (float) distance);
				}
			} else if (buffer > 0) {
				buffer -= .25;
			}
		}
	}
}

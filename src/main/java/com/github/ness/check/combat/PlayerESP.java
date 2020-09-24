package com.github.ness.check.combat;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.data.ImmutableVector;
import com.github.ness.utility.Utility;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class PlayerESP extends Check {

	private final double minangle;

	public static final CheckInfo checkInfo = CheckInfos.asyncPeriodic(Duration.ofMillis(700));

	public PlayerESP(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.minangle = this.ness().getMainConfig().getCheckSection().playerESP().minAngle();
	}
	
	public interface Config {
		@DefaultDouble(-0.05)
		double minAngle();
	}

	@Override
	protected void checkAsyncPeriodic() {
		runTaskLater(() -> {
			for (Player cheater : Bukkit.getOnlinePlayers()) {
				ImmutableVector direction = player().getMovementValues().getTo().getDirectionVector();
				for (Player tohide : Bukkit.getOnlinePlayers()) {
					if ((Utility.getAngle(cheater, tohide.getLocation(), direction) < minangle
							|| !cheater.hasLineOfSight(tohide))
							&& cheater.getLocation().distance(tohide.getLocation()) > 13) {
						cheater.hidePlayer(NESSAnticheat.getInstance(), tohide);
					} else {
						cheater.showPlayer(NESSAnticheat.getInstance(), tohide);
					}
				}
			}
		}, Duration.ZERO);
	}

}

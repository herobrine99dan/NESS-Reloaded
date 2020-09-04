package com.github.ness.check.combat;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ness.CheckManager;
import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.ImmutableVector;
import com.github.ness.utility.Utility;

public class PlayerESP extends AbstractCheck<PlayerInteractEvent> {

	final double minangle;

	public PlayerESP(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 700, TimeUnit.MILLISECONDS));
		this.minangle = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getDouble("minangle", -0.05);
	}

	@Override
	protected void checkAsyncPeriodic(NessPlayer player) {
		(new BukkitRunnable() {
			public void run() {
				for (Player cheater : Bukkit.getOnlinePlayers()) {
					ImmutableVector direction = player.getMovementValues().getTo().getDirectionVector();
					for (Player tohide : Bukkit.getOnlinePlayers()) {
						if ((Utility.getAngle(cheater, tohide.getLocation(), direction) < minangle)
								&& cheater.getLocation().distance(tohide.getLocation()) > 9) {
							cheater.hidePlayer(NESSAnticheat.getInstance(), tohide);
						} else {
							cheater.showPlayer(NESSAnticheat.getInstance(), tohide);
						}
					}
				}
			}
		}).runTask(NESSAnticheat.getInstance());
	}

}

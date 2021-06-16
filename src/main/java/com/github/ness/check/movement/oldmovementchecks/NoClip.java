package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class NoClip extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public NoClip(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();
		Double dist = Double.valueOf(from.distance(to));
		Double hozDist = Double.valueOf(dist.doubleValue() - to.getY() - from.getY());
		boolean surrounded = true;
		for (int x2 = -2; x2 <= 2; x2++) {
			for (int y = -2; y <= 3; y++) {
				for (int z3 = -2; z3 <= 2; z3++) {
					Material belowSel2 = player.getWorld().getBlockAt(player.getLocation().add(x2, y, z3)).getType();
					if (!belowSel2.isSolid())
						surrounded = false;
				}
			}
		}
		if (surrounded && (hozDist.doubleValue() > 0.2D || to.getBlockY() < from.getBlockY())) {
			this.flagEvent(event);
		}
	}

}

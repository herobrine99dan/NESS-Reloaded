package com.github.ness.check.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class GhostHand extends AbstractCheck<PlayerInteractEvent> {

	public static final CheckInfo<PlayerInteractEvent> checkInfo = CheckInfo.eventOnly(PlayerInteractEvent.class);

	public GhostHand(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerInteractEvent e) {
		Check(e);

	}

	/**
	 * Check for Ghost Hand
	 *
	 * @param event
	 */
	public void Check(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		final Location loc = player.getLocation();
		Block targetBlock = player.getTargetBlock(null, 7);
		NessPlayer p = player();
		if (targetBlock.getLocation().add(0, 1, 0).getBlock().getType().name().toLowerCase().contains("slab")) {
			return;
		}
		if (event.getClickedBlock() == null || event.getBlockFace() == null) {
			return;
		}
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& targetBlock.getType().isSolid() && targetBlock.getType().isOccluding()
				&& p.getMovementValues().XZDiff < 0.15 && !targetBlock.equals(event.getClickedBlock())) {
			Location block = event.getClickedBlock().getLocation().add(event.getBlockFace().getModX(),
					event.getBlockFace().getModY(), event.getBlockFace().getModZ());
			Bukkit.getScheduler().runTaskLater(this.ness(), () -> {

				Location loc1 = player.getLocation();
				float grade = Math.abs(loc.getYaw() - loc1.getYaw()) + Math.abs(loc.getPitch() - loc1.getPitch());

				if (!(grade == 0)) {
					return;
				}
				if (block.getBlock().getType().isSolid() || !targetBlock.equals(event.getClickedBlock())) {
					if (player().setViolation(new Violation("GhostHand", "")))
						event.setCancelled(true);
				}
			}, 2L);
		}
	}

}

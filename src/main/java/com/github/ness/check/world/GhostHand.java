package com.github.ness.check.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.raytracer.RayCaster;

public class GhostHand extends ListeningCheck<PlayerInteractEvent> {

	public static final ListeningCheckInfo<PlayerInteractEvent> checkInfo = CheckInfos
			.forEvent(PlayerInteractEvent.class);
	private final Location EMPTYLOCATION = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);

	public GhostHand(ListeningCheckFactory<?, PlayerInteractEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerInteractEvent e) {
		Check(e);

	}

	// Using two raycaster, first we try the bukkit's one, it is the more aggressive
	// and if it flags, we try the custom raytracer
	// If the custom raytracer also flags, then flag the check
	public void Check(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		NessPlayer nessPlayer = player();
		if (event.getClickedBlock() == null || event.getBlockFace() == null) {
			return;
		}
		if ((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Location fixedEyeLocation = event.getPlayer().getEyeLocation().subtract(0.0D, 0.0, 0.0D);
			// int interactedBlockCorrect = traceLocation(fixedEyeLocation,
			// event.getBlock().getLocation(), 6,
			// event.getBlock());
			int interactedBlockCorrect = traceLocation1(event.getPlayer().getLocation().getDirection(),
					fixedEyeLocation, 6, event.getClickedBlock());
			if (interactedBlockCorrect > 0) {
				this.flag("val: " + interactedBlockCorrect);
			}
		}
	}

	private int traceLocation1(Vector direction, Location from, float maxDistance, Block blockToFind) {
		int impossibleLocations = 0;
		for (double i = 0; i < maxDistance; i+=0.1) {
			Location newLoc = direction.clone().normalize().multiply(i).add(from.toVector()).toLocation(from.getWorld());
			if (newLoc.getBlock().equals(blockToFind)) {
				return impossibleLocations;
			}
			if (newLoc.getBlock().getType().isOccluding() && newLoc.getBlock().getType().isSolid()) {
				impossibleLocations++;
			}
		}
		return impossibleLocations;
	}

}

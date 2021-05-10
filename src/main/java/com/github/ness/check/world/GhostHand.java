package com.github.ness.check.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
			Location bukkitBlock = getBukkitBlock(player);
			Location rayTraceBlock = getRayTracerBlock(player);
			Location otherRayTracerBlock = getOtherRayTracerBlock(player);
			int correctness = 0;
			if (bukkitBlock.equals(rayTraceBlock)) {
				correctness++;
			}
			if (bukkitBlock.equals(otherRayTracerBlock)) {
				correctness++;
			}
			if (rayTraceBlock.equals(otherRayTracerBlock)) {
				correctness++;
			}
			nessPlayer.sendDevMessage("otherRayTracerBlock: " + otherRayTracerBlock);
			double percentage = ((double) correctness / 3.0) * 100;
			nessPlayer
					.sendDevMessage("GhostHand Interaction Percentage: " + percentage + " correctness: " + correctness);
		}
	}

	private Location getBukkitBlock(Player player) {
		return player.getTargetBlock(null, 6).getLocation();
	}

	private Location getRayTracerBlock(Player player) {
		final RayCaster customCaster = new RayCaster(player, 6, RayCaster.RaycastType.BLOCK, this.ness()).compute();
		return customCaster.getBlockFound() != null ? customCaster.getBlockFound().getLocation() : EMPTYLOCATION;
	}

	private Location getOtherRayTracerBlock(Player player) {
		Location fixedEyeLocation = player.getEyeLocation().subtract(0.0D, 1, 0.0D);
		Vector direction = fixedEyeLocation.getDirection();
		final int range = 6;
		for (int i = 1; i <= range; i++) {
			Location loc = fixedEyeLocation.add(direction);
			if (!loc.getBlock().getType().isOccluding())
				return loc.getBlock().getLocation();
			if (i == range)
				return loc.getBlock().getLocation();
		}
		return EMPTYLOCATION;
	}

}

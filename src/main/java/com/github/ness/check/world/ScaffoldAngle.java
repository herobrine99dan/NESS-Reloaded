package com.github.ness.check.world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class ScaffoldAngle extends ListeningCheck<BlockPlaceEvent> {
	private static final double MAX_ANGLE = Math.toRadians(90);

	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEvent(BlockPlaceEvent.class);

	public ScaffoldAngle(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(BlockPlaceEvent event) {
		BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
		if (placedFace == null) {
			return;
		}
		NessPlayer nessPlayer = player();
		Material material = getItemInHand(event.getPlayer());
		if ((event.getBlockPlaced().getType() != material) && material.isSolid() && material.isOccluding()) {
			flagEvent(event);
			// if(player().setViolation(new Violation("Scaffold", "Impossible")))
			// event.setCancelled(true);

			return;
		}
		final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
		float placedAngle = nessPlayer.getMovementValues().getTo().getDirectionVector().toBukkitVector()
				.angle(placedVector);
		if (placedAngle > MAX_ANGLE) {
			flagEvent(event);
			// if(player().setViolation(new Violation("Scaffold", "HighAngle")))
			// event.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	private Material getItemInHand(Player p) {
		if(Bukkit.getVersion().contains("1.8")) {
			return p.getItemInHand().getType(); //1.8 Version doesn't have the second hand!
		} else {
			PlayerInventory inventory = p.getInventory();
			if(inventory.getItemInMainHand().getType().isBlock()) {
				return inventory.getItemInMainHand().getType();
			}
			return inventory.getItemInOffHand().getType();
		}
	}

}

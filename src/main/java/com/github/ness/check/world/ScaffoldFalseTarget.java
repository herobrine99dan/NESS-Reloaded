package com.github.ness.check.world;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;

public class ScaffoldFalseTarget extends ListeningCheck<BlockPlaceEvent> {

	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEvent(BlockPlaceEvent.class);

	public ScaffoldFalseTarget(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double buffer;

	@Override
	protected void checkEvent(BlockPlaceEvent e) {
		Check1(e);
	}

	public void Check1(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block target = player.getTargetBlock(null, 6);
		Material targetMaterial = this.ness().getMaterialAccess().getMaterial(target);
		Material underMaterial = this.ness().getMaterialAccess()
				.getMaterial(event.getBlock().getLocation().subtract(0.0D, 1.0D, 0.0D));
		if (target == null) {
			return;
		}
		//TODO Test this with getLastTwoTargetBlocks
		if (underMaterial.name().contains("AIR")) {
			if (!event.getBlock().getLocation().equals(target.getLocation()) && !event.isCancelled()
					&& targetMaterial.isSolid() && player.getLocation().getY() > event.getBlock().getLocation().getY()
					&& targetMaterial.isOccluding()) {
				if (++buffer > 1) {
					flagEvent(event);
				}
				// if(player().setViolation(new Violation("Scaffold", "FalseTarget")))
				// event.setCancelled(true);
			} else if (buffer > 0) {
				buffer -= 0.25;
			}
		}
		if (Utility
				.getMaterialName(event.getBlock().getWorld()
						.getBlockAt(event.getBlock().getLocation().subtract(0.0D, 1.0D, 0.0D)).getLocation())
				.contains("AIR")) {
			if (!event.getBlock().getLocation().equals(target.getLocation()) && !event.isCancelled()
					&& target.getType().isSolid() && !target.getType().name().toLowerCase().contains("sign")
					&& !target.getType().toString().toLowerCase().contains("fence")
					&& player.getLocation().getY() > event.getBlock().getLocation().getY()
					&& target.getType().isOccluding()) {
				if (++buffer > 1) {
					flagEvent(event);
				}
				// if(player().setViolation(new Violation("Scaffold", "FalseTarget")))
				// event.setCancelled(true);
			} else if (buffer > 0) {
				buffer -= 0.25;
			}
		}
	}
}

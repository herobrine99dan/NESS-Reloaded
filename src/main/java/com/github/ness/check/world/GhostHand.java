package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.raytracer.RayCaster;

public class GhostHand extends ListeningCheck<PlayerInteractEvent> {

	public static final ListeningCheckInfo<PlayerInteractEvent> checkInfo = CheckInfos
			.forEvent(PlayerInteractEvent.class);

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
		final Block targetBlock = player.getTargetBlock(null, 7);
		NessPlayer nessPlayer = player();
		if (event.getClickedBlock() == null || event.getBlockFace() == null) {
			return;
		}
		if ((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if (!targetBlock.equals(event.getClickedBlock())) {
				final RayCaster customCaster = new RayCaster(event.getPlayer(), 6, RayCaster.RaycastType.BLOCK,
						this.ness()).compute();
				if (customCaster.getBlockFound() != null) {
					if (!customCaster.getBlockFound().equals(event.getClickedBlock()) && player.getLocation().distance(targetBlock.getLocation()) > 1) {
						this.flagEvent(event, "targetBlock: " + targetBlock.getType().name() + " customRayCaster:  "
								+ customCaster.getBlockFound().getType().name());
					}
				}

			}
		}
	}

}

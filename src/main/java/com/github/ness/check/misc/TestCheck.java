package com.github.ness.check.misc;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.raytracer.RayCaster;

public class TestCheck extends ListeningCheck<PlayerInteractEvent> {

	public static final ListeningCheckInfo<PlayerInteractEvent> checkInfo = CheckInfos
			.forEvent(PlayerInteractEvent.class);

	public TestCheck(ListeningCheckFactory<?, PlayerInteractEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerInteractEvent event) {
		/*if ((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			RayCaster customCaster = new RayCaster(event.getPlayer(), 6, RayCaster.RaycastType.BLOCK, this.ness());
			RayCaster bukkitCaster = new RayCaster(event.getPlayer(), 6, RayCaster.RaycastType.BLOCKBukkit,
					this.ness());
			customCaster.compute();
			bukkitCaster.compute();
			NessPlayer nessPlayer = this.player();
			if (customCaster.getBlockFounded() != null && bukkitCaster.getBlockFounded() != null) {
				if (!customCaster.getBlockFounded().getType().name()
						.equals(bukkitCaster.getBlockFounded().getType().name())) {
					nessPlayer.sendDevMessage("customCaster: " + customCaster.getBlockFounded().getType().name()
							+ " bukkitCaster: " + bukkitCaster.getBlockFounded().getType().name());
				}
			}
		}*/
	}

}

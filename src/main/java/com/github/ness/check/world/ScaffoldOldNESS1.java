package com.github.ness.check.world;

import java.time.Duration;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.PeriodicTaskInfo;

public class ScaffoldOldNESS1 extends ListeningCheck<BlockPlaceEvent> {

	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos
			.forEventWithTask(BlockPlaceEvent.class, PeriodicTaskInfo.syncTask(Duration.ofSeconds(1)));

	public ScaffoldOldNESS1(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private int placeTicks;

	@Override
	protected void checkEvent(BlockPlaceEvent e) {
		Check1(e);
	}

	@Override
	protected void checkSyncPeriodic() {
		placeTicks = 0;
	}

	public void Check1(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.isSneaking() && !player.isFlying() && this.player().getMovementValues().isGroundAround()) {
			if (event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0, 1, 0))
					.getType() == Material.AIR) {
				if (player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).equals(event.getBlock())) {
					if (placeTicks > 2) {
						this.flagEvent(event);
					}
				}
			}
		}
		placeTicks++;
	}
}

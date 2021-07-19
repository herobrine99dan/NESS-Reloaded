package com.github.ness.check.world;

import java.time.Duration;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.MultipleListeningCheck;
import com.github.ness.check.MultipleListeningCheckFactory;
import com.github.ness.check.PeriodicTaskInfo;

public class ScaffoldOldNESS1 extends MultipleListeningCheck {

	public static final CheckInfo checkInfo = CheckInfos.forMultipleEventListenerWithTask(
			PeriodicTaskInfo.syncTask(Duration.ofSeconds(1)), BlockPlaceEvent.class, PlayerMoveEvent.class);

	public ScaffoldOldNESS1(MultipleListeningCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	private int placeTicks;
	private int extremeYawTicks;

	@Override
	protected void checkEvent(Event event) {
		if(event instanceof BlockPlaceEvent) onPlace((BlockPlaceEvent) event);
		if(event instanceof PlayerMoveEvent) onMove((PlayerMoveEvent) event);
	}

	@Override
	protected void checkSyncPeriodic() {
		placeTicks = 0;
	}
	
	private void onMove(PlayerMoveEvent event) {
		if(Math.abs(this.player().getMovementValues().getPitchDiff()) > 30) {
			extremeYawTicks++;
		} else if(extremeYawTicks > 0) {
			extremeYawTicks--;
		}
	}

	private void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.isSneaking() && !player.isFlying() && this.player().getMovementValues().isGroundAround()) {
			if (event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0, 1, 0))
					.getType() == Material.AIR) {
				if (player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).equals(event.getBlock())) {
					if (extremeYawTicks <= 5) {
						this.flagEvent(event, " extremeYaw!");
					}
					if (placeTicks > 2) {
						this.flagEvent(event, " highPlaceTicks");
					}
				}
			}
		}
		placeTicks++;
	}
}

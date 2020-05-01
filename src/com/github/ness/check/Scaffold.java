package com.github.ness.check;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.PlayerManager;

import com.github.ness.CheckManager;
import com.github.ness.Violation;

public class Scaffold extends AbstractCheck<BlockPlaceEvent>{
	
	public Scaffold(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(BlockPlaceEvent e) {
       Check(e);
       Check1(e);
       Check2(e);
	}

	public  void Check(BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		final Block target = player.getTargetBlock((Set<Material>) null, 5);
		if (event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0.0, 1.0, 0.0))
				.getType() == Material.AIR) {
			if (!event.getBlock().getLocation().equals((Object) target.getLocation()) && !event.isCancelled()
					&& target.getType().isSolid() && !target.getType().name().toLowerCase().contains("sign")
					&& !target.getType().toString().toLowerCase().contains("fence")
					&& player.getLocation().getY() > event.getBlock().getLocation().getY()) {
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold"));
			}
			if (NESS.main.oldLoc.containsKey(player)
					&& NESS.main.oldLoc.get(player).getY() == player.getLocation().getY() && !player.isSneaking()
					&& !player.isFlying() && PlayerManager.groundAround(player.getLocation())
					&& PlayerManager.getAction("placeTicks", player) > 2.0 && player.getWorld()
							.getBlockAt(player.getLocation().subtract(0.0, 1.0, 0.0)).equals(event.getBlock())) {
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold"));
			}
		} else if (event.getBlock().getType() == event.getBlock().getWorld()
				.getBlockAt(event.getBlock().getLocation().subtract(0.0, 1.0, 0.0)).getType()
				&& NESS.main.oldLoc.get(player).getX() == player.getLocation().getX()
				&& NESS.main.oldLoc.get(player).getZ() == player.getLocation().getZ()
				&& NESS.main.oldLoc.get(player).getY() < player.getLocation().getY()
				&& PlayerManager.getAction("vPlaceTicks", player) > 2.0 && !player.isFlying() && player.getWorld()
						.getBlockAt(player.getLocation().subtract(0.0, 1.0, 0.0)).equals(event.getBlock())) {
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold"));
		}
		if (!player.isSneaking() && !player.isFlying() && PlayerManager.groundAround(player.getLocation())
				&& event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0.0, 1.0, 0.0))
						.getType() == Material.AIR
				&& player.getWorld().getBlockAt(player.getLocation().subtract(0.0, 1.0, 0.0))
						.equals(event.getBlock())) {
			if (PlayerManager.timeSince("extremeYaw", player) <= 250.0) {
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold"));
			}
			if (PlayerManager.getAction("placeTicks", player) > 2.0) {
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold"));
			}
		}
		if (player.getWorld().getBlockAt(player.getLocation().subtract(0.0, 1.0, 0.0)).equals(event.getBlock())) {
			PlayerManager.addAction("vPlaceTicks", player);
		}
	}

	public  void Check1(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		final double MAX_ANGLE = Math.toRadians(90);
		BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
		final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
		float placedAngle = player.getLocation().getDirection().angle(placedVector);

		if (placedAngle > MAX_ANGLE) {
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold"));
		}
	}

	public  void Check2(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		final float now = player.getLocation().getPitch();
		Bukkit.getScheduler().runTaskLater(NESS.main, new Runnable() {
			public void run() {
				float pitchNow = player.getLocation().getPitch();
				float diff = Math.abs(now - pitchNow);
				if (diff > 20F) {
					manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold"));
				}
			}
		}, 2L);
	}
}

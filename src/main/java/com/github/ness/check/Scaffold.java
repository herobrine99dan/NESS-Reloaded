package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

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

	public void Check(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlockPlaced();
		Block target = p.getTargetBlock(null, 6);
		if(event.getBlockAgainst().getType().name().toLowerCase().contains("fence") || event.getBlockPlaced().getType().name().toLowerCase().contains("ladder")) {
			return;
		}
		   if(!(target.getY()==b.getY() && target.getX()==b.getX() && target.getZ()==b.getZ())) {
			   manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold","InvalidBlock"));
				try {
					ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
							.getConfigurationSection("cancel");
					if (manager.getPlayer(p).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()), 0) > cancelsec.getInt("vl",10)) {
						event.setCancelled(true);
					}
				}catch(Exception ex) {}
		  }		
	}

	public void Check1(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		final double MAX_ANGLE = Math.toRadians(90);
		BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
		final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
		float placedAngle = player.getLocation().getDirection().angle(placedVector);

		if (placedAngle > MAX_ANGLE) {
			try {
				ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
						.getConfigurationSection("cancel");
				if (manager.getPlayer(player).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()), 0) > cancelsec.getInt("vl",10)) {
					event.setCancelled(true);
				}
			}catch(Exception ex) {}
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold","HighAngle"));
		}
	}

	public void Check2(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		final float now = player.getLocation().getPitch();
		Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
			float pitchNow = player.getLocation().getPitch();
			float diff = Math.abs(now - pitchNow);
			if (diff > 20F) {
				try {
					ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
							.getConfigurationSection("cancel");
					if (manager.getPlayer(player).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()), 0) > cancelsec.getInt("vl",10)) {
						event.setCancelled(true);
					}
				}catch(Exception ex) {}
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold","HighPitch"));
			}
		}, 2L);
	}
	
	
	
}

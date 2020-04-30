package org.mswsplex.MSWS.NESS;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ChestEsp {

	@SuppressWarnings("deprecation")
	public static void Check(Player p) {
		Location playerloc = p.getLocation();
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(NESS.main, new Runnable() {
			public void run() {
				World world = p.getWorld();
				int r = 16;
				for (Chunk c : world.getLoadedChunks()) {
					for (int x = (r * -1); x <= r; x++) {
						for (int y = (r * -1); y <= r; y++) {
							for (int z = (r * -1); z <= r; z++) {
								// Grab the current block
								Block b = c.getBlock(x, y, z);
								Location blockloc = b.getLocation();
								if (b.getType().name().toLowerCase().contains("chest")) {
									if (playerloc.distanceSquared(blockloc) > 30) {
										//p.sendMessage("Un blocco di tipo chest è stato bloccato a queste coordinate: "
												//+ blockloc + " con questa distanza dal player: "
												//+ playerloc.distanceSquared(blockloc));
										p.sendBlockChange(blockloc, Material.AIR, (byte) 0);
									} else {
										p.sendBlockChange(blockloc, b.getType(), b.getData());
									}
								}
							}
						}
					}
				}
			}
		}, 20L, 20L);
	}

	@SuppressWarnings("deprecation")
	protected static void Loop(Player p, int r, boolean ore) {
		/*
		 * new BukkitRunnable() {
		 * 
		 * @Override public void run() { new BukkitRunnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub }
		 * 
		 * }.runTask(NESS.main); } }.runTaskAsynchronously(NESS.main);
		 */
		NESS.main.getServer().getScheduler().scheduleAsyncDelayedTask(NESS.main, new Runnable() {
			public void run() {
				NESS.main.getServer().getScheduler().scheduleSyncDelayedTask(NESS.main, new Runnable() {
					public void run() {
						Location playerPos = p.getLocation();
						// Loop through all blocks within the radius (cube, not sphere)
						for (int x = (r * -1); x <= r; x++) {
							for (int y = (r * -1); y <= r; y++) {
								for (int z = (r * -1); z <= r; z++) {
									// Grab the current block
									Block b = playerPos.getWorld().getBlockAt(playerPos.getBlockX() + x,
											playerPos.getBlockY() + y, playerPos.getBlockZ() + z);
									Location loc = new Location(p.getWorld(), playerPos.getBlockX() + x,
											playerPos.getBlockY() + y, playerPos.getBlockZ() + z);
									// Is this chest?
									if (!ore) {
										if (b.getType().equals(Material.CHEST)
												|| b.getType().equals(Material.ENDER_CHEST)) {
											// p.sendMessage("Un blocco di tipo chest è stato bloccato a queste
											// coordinate: " + loc + " con questa distanza dal player: " +
											// playerPos.distanceSquared(loc));
											if (playerPos.distanceSquared(loc) > 30) {
												p.sendBlockChange(loc, Material.AIR, (byte) 0);
											} else {
												p.sendBlockChange(loc, b.getType(), b.getData());
											}
										}
									} else {
										if (b.getType().equals(Material.DIAMOND_ORE)
												|| b.getType().equals(Material.EMERALD_ORE)
												|| b.getType().equals(Material.IRON_ORE)
												|| b.getType().equals(Material.GOLD_ORE)) {
											if (playerPos.distanceSquared(loc) > 7) {
												p.sendBlockChange(loc, Material.AIR, (byte) 0);
											} else {
												p.sendBlockChange(loc, b.getType(), b.getData());
											}
										}
									}

								}
							}
						}
					}
				});
			}
		});
	}

}

package com.github.ness.check;

import org.bukkit.event.world.ChunkLoadEvent;

import com.github.ness.CheckManager;

public class ChestESP extends AbstractCheck<ChunkLoadEvent> {
	public ChestESP(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(ChunkLoadEvent.class));
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	@Override
	void checkEvent(ChunkLoadEvent event) {
		/*try {
			Bukkit.getScheduler().runTaskAsynchronously(manager.getNess(), () -> {
				int X = event.getChunk().getX() * 16;
				int Z = event.getChunk().getZ() * 16;
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						for (int y = 0; y < 128; y++) {
							Block b = event.getWorld().getBlockAt(X + x, y, Z + z);
							if (b.getType().name().toLowerCase()
									.contains("chest")
									|| b.getType().name().toLowerCase()
											.contains("ore")) {
								for (Entity e : event.getChunk().getEntities()) {
									if (e instanceof Player) {
										Player p = (Player) e;
										if (p.getLocation().distanceSquared(b.getLocation()) > 30.0D) {
											p.sendBlockChange(b.getLocation(), Material.AIR, (byte) 0);
										} else {
											p.sendBlockChange(b.getLocation(), b.getType(), b.getData());
										}
									}
								}
							}
						}
					}
				}
			});
		} catch (Exception ex) {
		}*/
	}

}

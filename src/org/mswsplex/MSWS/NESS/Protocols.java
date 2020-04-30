package org.mswsplex.MSWS.NESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.MSWS.NESS.checks.killaura.KillauraBotCheck;
import org.mswsplex.MSWS.NESS.checks.movement.BadPackets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;

public class Protocols {
	public static Map<UUID, Location> angles;

	static {
		Protocols.angles = new HashMap<UUID, Location>();
	}
	
	    private void use_entityhandle(PacketEvent event) {
	    	KillauraBotCheck.Check1(event);
	    }
	
	public Protocols() {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				(PacketListener) new PacketAdapter(NESS.main, new PacketType[] { PacketType.Play.Client.POSITION }) {
					public void onPacketReceiving(final PacketEvent event) {
						final Player player = event.getPlayer();
							PlayerManager.addAction("timerTick", player);
					}
				});
		ProtocolLibrary.getProtocolManager().addPacketListener(
				(PacketListener) new PacketAdapter(NESS.main, new PacketType[] { PacketType.Play.Client.USE_ENTITY }) {
					public void onPacketReceiving(final PacketEvent event) {
						final UUID u = event.getPlayer().getUniqueId();
						if (event.getPlayer() == null) {
							return;
						}
						final Location l = event.getPlayer().getLocation().clone();
						use_entityhandle(event);
						Protocols.angles.put(u, l);
					}
				});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(NESS.main,
				new PacketType[] { PacketType.Play.Client.CUSTOM_PAYLOAD }) {
			public void onPacketReceiving(final PacketEvent event) {
				if(event.isAsync() || event.isAsynchronous()) {
					return;
				}
				final Block target = event.getPlayer().getTargetBlock((Set<Material>) null, 5);
				if (target.getType() == Material.ANVIL) {
					return;
				}
				PlayerManager.addAction("packets", event.getPlayer());
				if (PlayerManager.getAction("packets", event.getPlayer()) > 10.0) {
					event.setCancelled(true);
				}
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(
				(PacketListener) new PacketAdapter(NESS.main, new PacketType[] { PacketType.Play.Client.SETTINGS }) {
					@SuppressWarnings("unchecked")
					public void onPacketReceiving(final PacketEvent event) {
						double lastClick = 0.0;
						double lastTime = 0.0;
						int times = 0;
						List<Double> packets = new ArrayList<Double>();
						if (PlayerManager.getInfo("packetTimes", (OfflinePlayer) event.getPlayer()) != null) {
							packets = (List<Double>) PlayerManager.getInfo("packetTimes",
									(OfflinePlayer) event.getPlayer());
						}
						packets.add(Double.valueOf(System.currentTimeMillis()));
						PlayerManager.setInfo("packetTimes", (OfflinePlayer) event.getPlayer(), packets);
						for (int i = 0; i < packets.size(); ++i) {
							final double d = packets.get(i);
							if (Math.abs(lastTime - (d - lastClick)) <= 5.0) {
								++times;
							}
							if (System.currentTimeMillis() - d > 5000.0) {
								packets.remove(i);
							}
							lastTime = d - lastClick;
							lastClick = d;
						}
						if (times > 5) {
							final int vl = times;
							Bukkit.getScheduler().scheduleSyncDelayedTask(NESS.main, () -> {
								WarnHacks.warnHacks(event.getPlayer(), "SkinBlinker", 5 * (vl - 4), -1.0, 55,"MorePackets",false);
							}, 0L);
						}
						if (PlayerManager.getAction("skinPackets", event.getPlayer()) > 10.0) {
							event.setCancelled(true);
						}
						PlayerManager.addAction("skinPackets", event.getPlayer());
					}
				});
	}
}

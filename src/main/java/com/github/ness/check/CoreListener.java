package com.github.ness.check;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public class CoreListener implements Listener {

	private final CheckManager manager;

	CoreListener(CheckManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent evt) {
		NessPlayer nessPlayer = manager.addPlayer(evt.getPlayer());
		
		nessPlayer.actionTime.put(PlayerAction.JOIN, System.nanoTime());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent evt) {
		Player player = evt.getPlayer();
		final long tenSecondsLater = 20L * 10L;
		NESSAnticheat ness = manager.getNess();
		ness.getServer().getScheduler().runTaskLater(ness, () -> {
			if (!player.isOnline()) {
				manager.removePlayer(player);
			}
		}, tenSecondsLater);
	}

	/*
	 * 
	 * Check helpers
	 * 
	 */

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent event) {
		Location destination = event.getTo();
		if (destination == null) {
			return;
		}
		String destinationWorld = destination.getWorld().getName();
		Location source = event.getFrom();
		String sourceWorld = source.getWorld().getName();
		if (!destinationWorld.equals(sourceWorld)) {
			return;
		}
		Player player = event.getPlayer();
		NessPlayer nessPlayer = manager.getExistingPlayer(player);
		if (nessPlayer == null) {
			return;
		}

		MovementValues values = new MovementValues(player, ImmutableLoc.of(destination, destinationWorld),
				ImmutableLoc.of(source, sourceWorld));
		nessPlayer.updateMovementValue(values);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVelocity(PlayerVelocityEvent event) {
		NessPlayer nessPlayer = manager.getExistingPlayer(event.getPlayer());
		if (nessPlayer == null) {
			return;
		}
		nessPlayer.velocity = ImmutableLoc.of(event.getVelocity().toLocation(event.getPlayer().getWorld()));
		nessPlayer.actionTime.put(PlayerAction.VELOCITY, System.nanoTime());
		if (nessPlayer.isDevMode()) {
			event.getPlayer().sendMessage("Velocity: " + nessPlayer.velocity);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockBreakEvent event) {
		NessPlayer nessPlayer = manager.getExistingPlayer(event.getPlayer());
		if (nessPlayer == null) {
			return;
		}
		if (Utility.getMaterialName(event.getBlock().getLocation()).contains("web")) {
			nessPlayer.actionTime.put(PlayerAction.WEBBREAKED, System.nanoTime());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTick(ReceivedPacketEvent event) {
		final String packetName = event.getPacket().getName().toLowerCase();
		if (packetName.contains("flying") || packetName.contains("position") || packetName.contains("look")) {
			event.getNessPlayer().onClientTick();
		}
	}

}

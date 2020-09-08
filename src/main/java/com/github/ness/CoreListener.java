package com.github.ness;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

public class CoreListener implements Listener {
	private final CheckManager manager;

	CoreListener(CheckManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		manager.getPlayer(event.getPlayer()).actionTime.put(PlayerAction.JOIN, System.nanoTime());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTick(ReceivedPacketEvent event) {
		final String packetName = event.getPacket().getName().toLowerCase();
		if (packetName.contains("flying") || packetName.contains("position") || packetName.contains("look")) {
			event.getNessPlayer().onClientTick();
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlace(BlockBreakEvent event) {
		if(Utility.getMaterialName(event.getBlock().getLocation()).contains("web")) {
			manager.getPlayer(event.getPlayer()).actionTime.put(PlayerAction.WEBBREAKED, System.nanoTime());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVelocity(PlayerVelocityEvent event) {
		NessPlayer nessPlayer = this.manager.getPlayer(event.getPlayer());
		nessPlayer.velocity = ImmutableLoc.of(event.getVelocity().toLocation(event.getPlayer().getWorld()));
		nessPlayer.actionTime.put(PlayerAction.VELOCITY, System.nanoTime());
		if (nessPlayer.isDevMode()) {
			event.getPlayer().sendMessage("Velocity: " + nessPlayer.velocity);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			manager.getPlayer((Player) event.getEntity()).actionTime.put(PlayerAction.DAMAGE,
					System.nanoTime());
		}
	}

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
		MovementValues values = new MovementValues(player, ImmutableLoc.of(destination, destinationWorld),
				ImmutableLoc.of(source, sourceWorld));
		manager.getPlayer(player).updateMovementValue(values);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent evt) {
		Player player = evt.getPlayer();
		final long tenSecondsLater = 20L * 10L;
		Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
			if (player.isOnline()) {
				manager.removePlayer(player);
			}
		}, tenSecondsLater);
	}

}

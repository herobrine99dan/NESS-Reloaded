package com.github.ness.check;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.NessPlayer;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

public class CoreListener implements Listener {

	private final PlayerManager manager;

	CoreListener(PlayerManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent evt) {
		NessPlayer nessPlayer = manager.addPlayer(evt.getPlayer());

		nessPlayer.setPlayerAction(PlayerAction.JOIN);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent evt) {
		Player player = evt.getPlayer();
		final long tenSecondsLater = 20L * 10L;

		JavaPlugin plugin = manager.getCheckManager().getNess().getPlugin();
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
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
		NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer(player);
		if (nessPlayer == null) {
			return;
		}

		MovementValues values = new MovementValues(player, ImmutableLoc.of(destination, destinationWorld),
				ImmutableLoc.of(source, sourceWorld));
		nessPlayer.updateMovementValue(values);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVelocity(PlayerVelocityEvent event) {
		NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer(event.getPlayer());
		if (nessPlayer == null) {
			return;
		}
		nessPlayer.setLastVelocity(ImmutableLoc.of(event.getVelocity().toLocation(event.getPlayer().getWorld())));
		nessPlayer.setPlayerAction(PlayerAction.VELOCITY);
		if (nessPlayer.isDevMode()) {
			event.getPlayer().sendMessage("Velocity: " + nessPlayer.getLastVelocity());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVelocity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer((Player) event.getDamager());
			if (nessPlayer == null) {
				return;
			}
			nessPlayer.setPlayerAction(PlayerAction.ATTACK);
			nessPlayer.setLastEntityAttacked(event.getEntity().getUniqueId());
		}
		if(event.getEntity() instanceof Player) {
			NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer((Player) event.getEntity());
			if (nessPlayer == null) {
				return;
			}
			nessPlayer.setPlayerAction(PlayerAction.DAMAGE);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSprint(PlayerToggleSprintEvent event) {
		NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer((Player) event.getPlayer());
		if (nessPlayer == null) {
			return;
		}
		nessPlayer.updateSprint(event.isSprinting());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer(event.getPlayer());
		if (nessPlayer == null) {
			return;
		}
		nessPlayer.setPlayerAction(PlayerAction.BLOCKBROKED);
		if (Utility.getMaterialName(event.getBlock().getLocation()).contains("WEB")) {
			nessPlayer.setPlayerAction(PlayerAction.WEBBREAKED);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent event) {
		NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer(event.getPlayer());
		if (nessPlayer == null) {
			return;
		}
		nessPlayer.setPlayerAction(PlayerAction.BLOCKPLACED);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTick(ReceivedPacketEvent event) {
		final String packetName = event.getPacket().getName().toLowerCase();
		if (packetName.contains("flying") || packetName.contains("position") || packetName.contains("look")) {
			event.getNessPlayer().onClientTick();
		}
	}

}

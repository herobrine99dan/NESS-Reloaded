package com.github.ness.check;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.NessPlayer;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

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

		MovementValues values = new MovementValues(nessPlayer, ImmutableLoc.of(destination, destinationWorld),
				ImmutableLoc.of(source, sourceWorld), this.manager.ness().getMaterialAccess());
		nessPlayer.updateMovementValue(values);
		if(player.isGliding() && this.manager.getCheckManager().getNess().getMinecraftVersion() > 189) {
			nessPlayer.setPlayerAction(PlayerAction.GLIDING);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVelocity(PlayerVelocityEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer(player);
		if (nessPlayer == null) {
			return;
		}
		nessPlayer.setPlayerAction(PlayerAction.VELOCITY);
		nessPlayer.setLastVelocity(ImmutableLoc.of(event.getVelocity().toLocation(player.getWorld())));
		if (nessPlayer.isDevMode()) {
			player.sendMessage("Velocity: " + nessPlayer.getLastVelocity());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVelocity(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity victim = event.getEntity();

		if (damager instanceof Player) {
			NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer((Player) damager);
			if (nessPlayer == null) {
				return;
			}
			nessPlayer.setPlayerAction(PlayerAction.ATTACK);
			nessPlayer.setLastEntityAttacked(victim.getUniqueId());
		}
		if (victim instanceof Player) {
			setPlayerAction((Player) victim, PlayerAction.DAMAGE);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer(event.getPlayer());
		if (nessPlayer == null) {
			return;
		}
		nessPlayer.setPlayerAction(PlayerAction.BLOCKBROKED);
		if (event.getBlock().getType().name().contains("WEB")) {
			nessPlayer.setPlayerAction(PlayerAction.WEBBREAKED);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent event) {
		setPlayerAction(event.getPlayer(), PlayerAction.BLOCKPLACED);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(PlayerAnimationEvent event) {
		setPlayerAction(event.getPlayer(), PlayerAction.ANIMATION);
	}

	private void setPlayerAction(Player player, PlayerAction action) {
		NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer(player);
		if (nessPlayer == null) {
			return;
		}
		nessPlayer.setPlayerAction(action);
	}

}

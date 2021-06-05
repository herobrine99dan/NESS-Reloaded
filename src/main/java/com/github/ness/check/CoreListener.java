package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import com.github.ness.NessPlayer;
import com.github.ness.blockgetter.MaterialAccess;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

public class CoreListener implements Listener {

	private final PlayerManager manager;
	private final MaterialAccess access;

	CoreListener(PlayerManager manager, MaterialAccess access) {
		this.manager = manager;
		this.access = access;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent evt) {
		NessPlayer nessPlayer = manager.addPlayer(evt.getPlayer());

		nessPlayer.setPlayerAction(PlayerAction.JOIN);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent evt) {
		manager.removePlayer(evt.getPlayer().getUniqueId());
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
				ImmutableLoc.of(source, sourceWorld), this.manager.ness().getMaterialAccess(),
				this.manager.ness().getMinecraftVersion());
		nessPlayer.updateMovementValue(values);
		if (this.manager.getCheckManager().getNess().getMinecraftVersion() > 189) {
			if (player.isGliding()) {
				nessPlayer.setPlayerAction(PlayerAction.GLIDING);
			}
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
			nessPlayer.getBukkitPlayer().sendMessage("CauseOfDamage: " + event.getCause().name());
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
	public void onInvOpen(InventoryOpenEvent event) {
		setPlayerAction(event.getPlayer(), PlayerAction.INVENTORYOPENED);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInvClosed(InventoryCloseEvent event) {
		setPlayerAction(event.getPlayer(), PlayerAction.INVENTORYCLOSED);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (event.getEntered() instanceof Player) {
			setPlayerAction((Player) event.getEntered(), PlayerAction.VEHICLEENTER);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAnimation(PlayerAnimationEvent event) {
		NessPlayer nessPlayer = setPlayerAction(event.getPlayer(), PlayerAction.ANIMATION);
		nessPlayer.setAnimationPacketsCounter(nessPlayer.getAnimationPacketsCounter() + 1);
	}

	private NessPlayer setPlayerAction(HumanEntity player, PlayerAction action) {
		NessPlayer nessPlayer = manager.getCheckManager().getExistingPlayer(player.getUniqueId());
		if (nessPlayer == null) {
			return null;
		}
		nessPlayer.setPlayerAction(action);
		return nessPlayer;
	}

}

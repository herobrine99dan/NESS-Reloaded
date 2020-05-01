package org.mswsplex.MSWS.NESS;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.mswsplex.MSWS.NESS.exploits.SimpleExploit;
import org.mswsplex.MSWS.NESS.protocol.Packet1_15Helper;

import com.github.ness.MovementPlayerData;
import com.github.ness.check.AntiASCII;
import com.github.ness.check.AntiTab;
import com.github.ness.check.AutoClicker;
import com.github.ness.check.FastEat;
import com.github.ness.check.FastPlace;
import com.github.ness.check.GhostHand;
import com.github.ness.check.IllegalInteraction;
import com.github.ness.check.InventoryHack;
import com.github.ness.check.Jesus;
import com.github.ness.check.NoSlowDown;
import com.github.ness.check.NoSwing;
import com.github.ness.check.Scaffold;
import com.github.ness.check.SpamBot;

public class MiscEvents implements Listener {
	@EventHandler
	public void commandProcess(final PlayerCommandPreprocessEvent event) {
		AntiASCII.Check1(event);
		PlayerManager.addAction("chatMessage", event.getPlayer());
	}
	
	@EventHandler
	public void onChat(final AsyncPlayerChatEvent event) {
		SpamBot.Check(event);
		AntiASCII.Check(event);
	}
	
	@EventHandler
	public void onSignChange(final SignChangeEvent event) {
		AntiASCII.Check2(event);
	}

	@EventHandler
	public void onChat(TabCompleteEvent event) {
		AntiTab.Check(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamaged(final EntityDamageEvent event) {
		if (event.getEntity() == null || !(event.getEntity() instanceof Player) || event.isCancelled()) {
			return;
		}
		final Player player = (Player) event.getEntity();
		PlayerManager.setAction("isHit", player, Double.valueOf(System.currentTimeMillis()));
	}

	@EventHandler
	public void onClickEvent(final InventoryClickEvent event) {
		EnchantHack.Check(event);
		InventoryHack.Check(event);
		InventoryHack.Check2(event);
	}

	@EventHandler
	public void onToggleFlight(final PlayerToggleFlightEvent event) {
		PlayerManager.setAction("wasFlight", event.getPlayer(), Double.valueOf(System.currentTimeMillis()));
	}
	
	@EventHandler
	public void onToggleSprint(final PlayerToggleSprintEvent event) {
		MovementPlayerData mp = MovementPlayerData.getInstance(event.getPlayer());
		mp.setSprintLastToggle(System.nanoTime());
		//PlayerManager.setAction("SprintTime", event.getPlayer(), Double.valueOf(System.nanoTime()));
	}

	@EventHandler
	public void onFoodConsumeEvent(PlayerItemConsumeEvent e) {
		NoSlowDown.FoodCheck(e);
	}

	@EventHandler
	public void onArrowShoot(EntityShootBowEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			NoSlowDown.ShootBowCheck(e);
		}
	}

	@EventHandler
	public void onBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		IllegalInteraction.Check(event);
		PlayerManager.setAction("breakTime", player, Double.valueOf(System.currentTimeMillis()));
	}

	@EventHandler
	public void onRegen(final EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			if (!player.hasPotionEffect(PotionEffectType.REGENERATION)
					&& !player.hasPotionEffect(PotionEffectType.SATURATION)
					&& !player.hasPotionEffect(PotionEffectType.HEAL)
					&& !player.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
				PlayerManager.addAction("regenTicks", player);
			}
		}
	}

	@EventHandler
	public void onFoodChange(final FoodLevelChangeEvent event) {
		FastEat.Check(event);
	}

	@EventHandler
	public void onPlaceBlock(final BlockPlaceEvent event) {
		IllegalInteraction.Check1(event);
		final Player player = event.getPlayer();
		Scaffold.Check(event);
		Scaffold.Check1(event);
		Scaffold.Check2(event);
		FastPlace.Check(event);
		PlayerManager.addAction("placeTicks", player);
		PlayerManager.setAction("sincePlace", player, Double.valueOf(System.currentTimeMillis()));
		if (event.getBlockReplacedState().getBlock().getType() == Material.WATER) {
			Jesus.placedBlockOnWater.add(event.getPlayer());
		}
	}

	@EventHandler
	public void onHotbarSwap(final PlayerItemHeldEvent event) {
		final Player player = event.getPlayer();
		@SuppressWarnings("deprecation")
		final ItemStack hand = player.getItemInHand();
		if (hand != null && hand.getType() == Material.BOW
				&& player.getInventory().containsAtLeast(new ItemStack(Material.ARROW), 1)) {
			PlayerManager.setInfo("blocking", (OfflinePlayer) player, true);
		}
		PlayerManager.setInfo("blocking", (OfflinePlayer) player, null);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	protected void onLeave(PlayerQuitEvent e) {
		NESSPlayer.removeInstance(e.getPlayer());
		if(NESS.main.newpacketssystem) {
			Packet1_15Helper.removePlayer(e.getPlayer());
		}
		if (Jesus.placedBlockOnWater.contains(e.getPlayer())) {
			Jesus.placedBlockOnWater.remove(e.getPlayer());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final Block target = player.getTargetBlock((Set<Material>) null, 5);
		GhostHand.Check(event);
		AutoClicker.Check(event);
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)
				&& !event.getClickedBlock().getLocation().equals((Object) target.getLocation())
				&& target.getType().isSolid() && !target.getType().name().toLowerCase().contains("sign")
				&& !target.getType().name().toLowerCase().contains("step") && target.getType() != Material.CACTUS
				&& PlayerManager.timeSince("longBroken", player) > 1000.0 && NESS.main.devMode) {
			MSG.tell((CommandSender) player,
					"&9Dev> &7type: " + target.getType() + " Solid: " + MSG.TorF(target.getType().isSolid()));
		}
		if (!event.isCancelled()
				&& (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			final ItemStack hand = player.getItemInHand();
			if (hand != null) {
				if (hand.getType().name().toLowerCase().contains("sword")) {
					PlayerManager.addAction("blocks", player);
				}
				if (hand.getType() == Material.BOW
						&& player.getInventory().containsAtLeast(new ItemStack(Material.ARROW), 1)) {
					PlayerManager.setInfo("blocking", (OfflinePlayer) player, true);
				}
			}
		}
		NESS.main.vl.set(player.getUniqueId() + ".accuracy.misses",
				(Object) (NESS.main.vl.getInt(player.getUniqueId() + ".accuracy.misses") + 1));
		NESS.main.lastLookLoc.put(player, target.getLocation());
		SimpleExploit.Check(event);
		NoSwing.interactEvent(event);
	}
	
	@EventHandler
	public void onAnimation(PlayerAnimationEvent event) {
		NoSwing.animationEvent(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEdit(PlayerEditBookEvent event) {
		SimpleExploit.Check1(event);
	}

	@EventHandler
	public void onShootBow(final ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			final Player player = (Player) event.getEntity().getShooter();
			if (event.getEntity().getType() == EntityType.ARROW) {
				final Vector vel = event.getEntity().getVelocity();
				final double totVel = Math.abs(vel.getX()) + Math.abs(vel.getY()) + Math.abs(vel.getZ());
				for (int i = 0; i < totVel; ++i) {
					PlayerManager.addAction("bowShots", player);
				}
				PlayerManager.setInfo("blocking", (OfflinePlayer) player, null);
			}
		}
	}

	@EventHandler
	public void onShift(final PlayerToggleSneakEvent event) {
		final Player player = event.getPlayer();
		PlayerManager.addAction("shiftTicks", player);
	}

	@EventHandler
	public void onTeleport(final PlayerTeleportEvent event) {
		final Player player = event.getPlayer();
		PlayerManager.setAction("teleported", player, Double.valueOf(System.currentTimeMillis()));
		Timer.timerLoc.remove(player);
	}

	@EventHandler
	public void onDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		PlayerManager.setAction("teleported", player, Double.valueOf(System.currentTimeMillis()));
		Timer.timerLoc.remove(player);
		if (Jesus.placedBlockOnWater.contains(event.getEntity())) {
			Jesus.placedBlockOnWater.remove(event.getEntity());
		}
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		NESSPlayer.getInstance(player);
		PlayerManager.setAction("lastJoin", player, Double.valueOf(System.currentTimeMillis()));
		SimpleExploit.Check2(event);
		if(NESS.main.newpacketssystem) {
			Packet1_15Helper.injectPlayer(event.getPlayer());
		}
		
	}
}

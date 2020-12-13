package com.github.ness.check;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ness.NessAnticheat;
import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.api.ChecksManager;
import com.github.ness.api.PlayersManager;
import com.github.ness.packets.PacketActor;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class CheckManager implements ChecksManager {

	private static final Logger logger = NessLogger.getLogger(CheckManager.class);

	private final NessAnticheat ness;
	private final PlayerManager playerManager;
	
	private volatile Set<BaseCheckFactory<?>> checkFactories;

	public CheckManager(NessAnticheat ness) {
		this.ness = ness;
		playerManager = new PlayerManager(this);
	}

	public NessAnticheat getNess() {
		return ness;
	}
	
	Collection<BaseCheckFactory<?>> getCheckFactories() {
		return checkFactories;
	}
	
	/*
	 * Start and stop
	 */

	public CompletableFuture<?> start() {
		logger.log(Level.FINE, "CheckManager starting...");
		JavaPlugin plugin = ness.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(playerManager.getListener(), plugin);

		return loadFactories(() -> {});
	}
	
	public CompletableFuture<?> reload() {
		Set<BaseCheckFactory<?>> factories = checkFactories;
		factories.forEach((factory) -> factory.close());

		playerManager.getPlayerCache().synchronous().invalidateAll();
		return loadFactories(() -> {
			JavaPlugin plugin = ness.getPlugin();
			ness.getPlugin().getServer().getScheduler().runTask(plugin, () -> {
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					playerManager.addPlayer(player);
				}
			});
		});
	}

	public void close() {
		HandlerList.unregisterAll(playerManager.getListener());
		checkFactories.forEach((factory) -> factory.close());
	}
	
	@Override
	public Collection<CheckFactory<?>> getAllChecks() {
		Set<CheckFactory<?>> result = new HashSet<>();
		Set<BaseCheckFactory<?>> checkFactories = this.checkFactories;
		if (checkFactories == null) {
			return Collections.emptySet();
		}
		for (BaseCheckFactory<?> factory : checkFactories) {
			if (factory instanceof CheckFactory) {
				result.add((CheckFactory<?>) factory);
			}
		}
		return Collections.unmodifiableSet(result);
	}
	
	public PlayersManager getPlayersManager() {
		return playerManager;
	}
	
	private CompletableFuture<?> loadFactories(Runnable whenComplete) {
		Collection<String> enabledCheckNames = ness.getMainConfig().getEnabledChecks();
		logger.log(Level.FINE, "Loading all check factories: {0}", enabledCheckNames);

		CompletableFuture<Set<BaseCheckFactory<?>>> checkCreationFuture;
		checkCreationFuture = CompletableFuture.supplyAsync(() -> {
			return new FactoryLoader(this, enabledCheckNames).createAllFactories();
		}, ness.getExecutor());
		return checkCreationFuture.whenComplete((checkFactories, ex) -> {

			if (ex != null) {
				logger.log(Level.SEVERE, "Failed to load check factories", ex);
				return;
			}
			for (BaseCheckFactory<?> factory : checkFactories) {
				factory.start();
			}
			logger.log(Level.FINE, "Started all check factories");
			this.checkFactories = checkFactories;
			whenComplete.run();
		});
	}
	
	/**
	 * Gets a player whose ness player is already loaded. The caller must null check the return value
	 * 
	 * @param player the bukkit player
	 * @return the ness player or {@code null} if not loaded
	 */
	public NessPlayer getExistingPlayer(Player player) {
		return getExistingPlayer(player.getUniqueId());
	}
	
	/**
	 * Gets a player by UUID whose ness player is already loaded. The caller must null check the return value
	 * 
	 * @param uuid the player UUID
	 * @return the ness player or {@code null} if not loaded
	 */
	public NessPlayer getExistingPlayer(UUID uuid) {
		return playerManager.getPlayer(uuid);
	}
	
	/**
	 * Do something for each NessPlayer
	 *
	 * @param action what to do
	 */
	public void forEachPlayer(Consumer<NessPlayer> action) {
		playerManager.getPlayerCache().synchronous().asMap().values().forEach((playerData) -> {
			action.accept(playerData.getNessPlayer());
		});
	}
	
	/**
	 * Do something for each check applying to a player. The iteration is consistently ordered
	 * 
	 * @param uuid the player UUID
	 * @param action what to do
	 */
	public void forEachCheck(UUID uuid, Consumer<Check> action) {
		for (BaseCheckFactory<?> checkFactory : checkFactories) {
			if (checkFactory instanceof CheckFactory) {
				Check check = ((CheckFactory<?>) checkFactory).getChecksMap().get(uuid);
				if (check != null) {
					action.accept(check);
				}
			}
		}
	}

	public PacketActor createPacketActor() {
		throw new UnsupportedOperationException("TODO");
	}

}

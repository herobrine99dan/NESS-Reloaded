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

import com.github.ness.NESSAnticheat;
import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.api.NESSApi;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class CheckManager {

	private static final Logger logger = NessLogger.getLogger(CheckManager.class);

	private final NESSAnticheat ness;
	private final PlayerManager playerManager;
	
	private volatile Set<BaseCheckFactory<?>> checkFactories;

	public CheckManager(NESSAnticheat ness) {
		this.ness = ness;
		playerManager = new PlayerManager(this);
	}

	public NESSAnticheat getNess() {
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
	
	/*
	 * API methods
	 */
	
	/**
	 * Implements {@link NESSApi#getAllChecks()}
	 * 
	 * @return all checks
	 */
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
	
	/**
	 * Implements {@link NESSApi#getAllPlayers()}
	 * 
	 * @return all ness players
	 */
	public Collection<NessPlayer> getAllPlayers() {
		Set<NessPlayer> result = new HashSet<>();
		for (NessPlayerData playerData  : playerManager.getPlayerCache().synchronous().asMap().values()) {
			result.add(playerData.getNessPlayer());
		}
		return Collections.unmodifiableSet(result);
	}
	
	/**
	 * Implements {@link NESSApi#getPlayer(UUID)}
	 * 
	 * @param uuid the player UUID
	 * @return the ness player
	 */
	public NessPlayer getPlayer(UUID uuid) {
		NessPlayerData playerData = playerManager.getPlayerCache().synchronous().getIfPresent(uuid);
		return (playerData == null) ? null : playerData.getNessPlayer();
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
		return getPlayer(player.getUniqueId());
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

}

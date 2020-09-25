package com.github.ness.check;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.github.ness.NESSAnticheat;
import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.api.NESSApi;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class CheckManager {

	private static final Logger logger = NessLogger.getLogger(CheckManager.class);

	private final NESSAnticheat ness;
	private final CoreListener coreListener;

	private final AsyncCache<UUID, NessPlayerData> playerCache;

	private volatile Set<BaseCheckFactory<?>> checkFactories;

	public CheckManager(NESSAnticheat ness) {
		this.ness = ness;
		coreListener = new CoreListener(this);
		//olyviypul99khu kvlzu'a sprl X2MtGtCwitB=
		playerCache = Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(5L))
				.removalListener(new PlayerCacheRemovalListener()).scheduler(Scheduler.systemScheduler()).buildAsync();
	}

	public NESSAnticheat getNess() {
		return ness;
	}

	public CompletableFuture<?> start() {
		logger.log(Level.FINE, "CheckManager starting...");
		ness.getServer().getPluginManager().registerEvents(coreListener, ness);

		return loadFactories(() -> {});
	}
	
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
		for (CompletableFuture<NessPlayerData> playerData  : playerCache.asMap().values()) {
			if (playerData.isDone()) {
				result.add(playerData.join().getNessPlayer());
			}
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
		CompletableFuture<NessPlayerData> playerData = playerCache.getIfPresent(uuid);
		return (playerData.isDone()) ? playerData.join().getNessPlayer() : null;
	}
	
	public CompletableFuture<?> reload() {
		Set<BaseCheckFactory<?>> factories = checkFactories;
		factories.forEach((factory) -> factory.close());

		playerCache.synchronous().invalidateAll();
		return loadFactories(() -> {
			ness.getServer().getScheduler().runTask(ness, () -> {
				for (Player player : ness.getServer().getOnlinePlayers()) {
					addPlayer(player);
				}
			});
		});
	}

	public void close() {
		HandlerList.unregisterAll(coreListener);
		checkFactories.forEach((factory) -> factory.close());
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

	NessPlayer addPlayer(Player player) {
		NessPlayer nessPlayer = new NessPlayer(player, ness.getMainConfig().isDevMode());

		Set<BaseCheckFactory<?>> enabledFactories = new HashSet<>();
		for (BaseCheckFactory<?> factory : checkFactories) {
			if (!player.hasPermission("ness.bypass." + factory.getCheckName())) {
				enabledFactories.add(factory);
			}
		}
		UUID uuid = player.getUniqueId();
		playerCache.get(uuid, (u, executor) -> {
			return CompletableFuture.supplyAsync(() -> {

				Set<Check> checks = new HashSet<>();
				for (BaseCheckFactory<?> factory : enabledFactories) {

					BaseCheck baseCheck = factory.newCheck(nessPlayer);
					if (baseCheck instanceof Check) {
						checks.add((Check) baseCheck);
					}
				}
				return new NessPlayerData(nessPlayer, checks);
			}).handle((value, ex) -> {
				if (ex != null) {
					logger.log(Level.SEVERE, "Failed to load checks for " + uuid, ex);
					return null;
				}
				logger.log(Level.FINE, "Successfully loaded checks for {0}", uuid);
				return value;
			});
		});
		return nessPlayer;
	}
	
	private static class PlayerCacheRemovalListener implements RemovalListener<UUID, NessPlayerData> {

		@Override
		public void onRemoval(@Nullable UUID key, @Nullable NessPlayerData playerData, @NonNull RemovalCause cause) {
			try (NessPlayer nessPlayer = playerData.getNessPlayer()) {
				for (Check check : playerData.getChecks()) {
					check.getFactory().removeCheck(nessPlayer);
				}
			}
		}
		
	}
	
	/**
	 * Gets a player whose ness player is already loaded. The caller must null check the return value
	 * 
	 * @param player the bukkit player
	 * @return the ness player or {@code null} if not loaded
	 */
	public NessPlayer getExistingPlayer(Player player) {
		NessPlayerData playerData = playerCache.synchronous().getIfPresent(player.getUniqueId());
		return (playerData == null) ? null : playerData.getNessPlayer();
	}

	/**
	 * Forcibly remove a player. This may be used to clear the player's data before
	 * automatic expiration of the 5 minute cache.
	 *
	 * @param player the player to remove
	 */
	void removePlayer(Player player) {
		logger.log(Level.FINE, "Forcibly removing player {0}", player);
		playerCache.synchronous().invalidate(player.getUniqueId());
	}

	/**
	 * Do something for each NESSPlayer
	 *
	 * @param action what to do
	 */
	public void forEachPlayer(Consumer<NessPlayer> action) {
		playerCache.asMap().values().forEach((playerData) -> {
			if (playerData.isDone()) {
				action.accept(playerData.join().getNessPlayer());
			}
		});
	}

}

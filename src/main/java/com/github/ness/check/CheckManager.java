package com.github.ness.check;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CheckManager {

	private static final Logger logger = Logger.getLogger(CheckManager.class.getName());

	private final CoreListener coreListener;
	private final Cache<UUID, NessPlayer> playerCache;
	private final ConcurrentMap<NessPlayer, Set<AbstractCheck<?>>> playerChecks = new ConcurrentHashMap<>();
	private final NESSAnticheat ness;

	private volatile Set<CheckFactory<?>> checkFactories;

	public CheckManager(NESSAnticheat ness) {
		this.ness = ness;
		coreListener = new CoreListener(this);

		playerCache = Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(5L))
				.removalListener(new RemovalListener<UUID, NessPlayer>() {

					@Override
					public void onRemoval(@Nullable UUID key, @Nullable NessPlayer value, @NonNull RemovalCause cause) {
						value.close();
					}

				}).build();
	}

	public NESSAnticheat getNess() {
		return ness;
	}

	public CompletableFuture<?> start() {
		ness.getServer().getPluginManager().registerEvents(coreListener, ness);

		return loadFactories(() -> {});
	}
	
	public CompletableFuture<?> reload() {
		Set<CheckFactory<?>> factories = checkFactories;
		factories.forEach((factory) -> factory.close());

		playerCache.invalidateAll();
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
		Collection<String> enabledCheckNames = ness.getNessConfig().getEnabledChecks();

		CompletableFuture<Set<CheckFactory<?>>> checkCreationFuture;
		checkCreationFuture = CompletableFuture.supplyAsync(() -> createAllFactories(enabledCheckNames),
				ness.getExecutor());
		return checkCreationFuture.whenComplete((checkFactories, ex) -> {

			if (ex != null) {
				logger.log(Level.SEVERE, "Failed to load check factories");
				return;
			}
			for (CheckFactory<?> factory : checkFactories) {
				factory.start();
			}
			this.checkFactories = checkFactories;
			whenComplete.run();
		});
	}

	private Set<CheckFactory<?>> createAllFactories(Collection<String> enabledCheckNames) {
		Set<CheckFactory<?>> factories = new HashSet<>();

		factoryLoadLoop: for (String checkName : enabledCheckNames) {
			for (ChecksPackage checksPackage : ChecksPackage.values()) {

				CheckFactory<?> factory = loadFactory(checksPackage.prefix(), checkName);
				if (factory != null) {
					factories.add(factory);
					continue factoryLoadLoop;
				}
			}
			logger.log(Level.WARNING, "No check class found for check " + checkName);
		}
		for (String requiredCheck : ChecksPackage.REQUIRED_CHECKS) {
			CheckFactory<?> factory = loadFactory("required", requiredCheck);
			if (factory == null) {
				logger.log(Level.WARNING, "No check class found for required check " + requiredCheck);
				continue;
			}
			factories.add(factory);
		}
		return factories;
	}

	@SuppressWarnings("unchecked")
	private <E extends Event, C extends AbstractCheck<E>> CheckFactory<C> loadFactory(String packagePrefix,
			String checkName) {
		return (CheckFactory<C>) new CheckFactoryCreator<>(this, packagePrefix, checkName).create();
	}

	NessPlayer addPlayer(Player player) {
		UUID uuid = player.getUniqueId();
		NessPlayer nessPlayer = new NessPlayer(player, ness.getNessConfig().isDevMode());

		Set<CheckFactory<?>> enabledFactories = new HashSet<>();
		for (CheckFactory<?> factory : checkFactories) {
			if (!player.hasPermission("ness.bypass." + factory.getCheckName())) {
				enabledFactories.add(factory);
			}
		}
		CompletableFuture.runAsync(() -> {
			Set<AbstractCheck<?>> checks = new HashSet<>();
			for (CheckFactory<?> factory : enabledFactories) {
				if (!player.hasPermission("ness.bypass." + factory.getCheckName())) {
					checks.add(factory.newCheck(nessPlayer));
				}
			}
			playerChecks.put(nessPlayer, checks);
		}, ness.getExecutor()).whenComplete((ignore, ex) -> {
			if (ex != null) {
				logger.log(Level.SEVERE, "Failed to load checks for " + uuid);
			}
		});

		playerCache.put(uuid, nessPlayer);
		return nessPlayer;
	}
	
	/**
	 * Gets a player whose ness player is already loaded. The caller must null check the return value
	 * 
	 * @param player the bukkit player
	 * @return the ness player or {@code null} if not loaded
	 */
	public NessPlayer getExistingPlayer(Player player) {
		return playerCache.getIfPresent(player.getUniqueId());
	}

	/**
	 * Forcibly remove NESSPlayer. This may be used to clear the NESSPlayer before
	 * automatic expiration of the 10 minute cache.
	 *
	 * @param player the player to remove
	 */
	void removePlayer(Player player) {
		logger.log(Level.FINE, "Forcibly removing player {0}", player);
		playerCache.invalidate(player.getUniqueId());
	}

	/**
	 * Do something for each NESSPlayer
	 *
	 * @param action what to do
	 */
	void forEachPlayer(Consumer<NessPlayer> action) {
		playerCache.asMap().values().forEach(action);
	}

}

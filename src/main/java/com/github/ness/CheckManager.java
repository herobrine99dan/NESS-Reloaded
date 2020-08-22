package com.github.ness;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.ness.check.AbstractCheck;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class CheckManager implements AutoCloseable {

	private final Cache<UUID, NessPlayer> playerCache = Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(5L)).build();

	final CoreListener coreListener;

	private volatile Set<AbstractCheck<?>> checks;

	private static final Logger logger = LogManager.getLogger(CheckManager.class);

	@Getter
	private final NESSAnticheat ness;

	CheckManager(NESSAnticheat ness) {
		this.ness = ness;
		coreListener = new CoreListener(this);
	}

	CompletableFuture<?> loadChecks() {
		return CompletableFuture.supplyAsync(this::getAllChecks, ness.getExecutor()).handleAsync((checks, ex1) -> {
			if (ex1 != null) {
				logger.error("Unable to instantiate new checks", ex1);
				return null;
			}
			processChecks(checks);
			return null;
		}).whenComplete((ignore, ex) -> {
			if (ex != null) {
				logger.error("Unable to load checks");
			}
		});
	}
	
	private void processChecks(Set<AbstractCheck<?>> checks) {
		logger.debug("Initiating all checks");
		checks.forEach(AbstractCheck::initiate);
		this.checks = checks;
	}

	private Set<AbstractCheck<?>> getAllChecks() {
		Set<AbstractCheck<?>> checks = new HashSet<>();
		for (String checkName : ness.getNessConfig().getEnabledChecks()) {
			if (!checkName.equals("AbstractCheck") && !checkName.equals("CheckInfo")) {
				try {
					Class<?> clazz = Class.forName("com.github.ness.check." + checkName);
					if (AbstractCheck.class.isAssignableFrom(clazz)) {
						Constructor<?> constructor = clazz.getDeclaredConstructor(CheckManager.class);
						checks.add((AbstractCheck<?>) constructor.newInstance(this));
					} else {
						// This is our fault
						logger.warn("Check class {} does not extend AbstractCheck", clazz);
					}
				} catch (ClassNotFoundException ex) {

					// No class found!
					// This is the user's fault
					logger.warn("Check {} from the config does not exist", checkName, ex);
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException ex) {

					// Reflection error!
					// This is our fault
					logger.warn("Could not instantiate check {}", checkName, ex);
				}
			}
		}
		return checks;
	}

	CompletableFuture<?> reloadChecks() {
		logger.debug("Reloading all checks");
		checks.forEach(AbstractCheck::close);
		return loadChecks();
	}

	@Override
	public void close() {
		logger.debug("Closing all checks");
		checks.forEach(AbstractCheck::close);
		checks.clear();
		HandlerList.unregisterAll(ness);
	}

	/**
	 * Gets a NessPlayer or creates one if it does not exist
	 * 
	 * @param player the corresponding player
	 * @return the ness player, never null
	 */
	public NessPlayer getPlayer(Player player) {
		return playerCache.get(player.getUniqueId(), (u) -> {
			logger.debug("Adding player {}", player);
			return new NessPlayer(player, ness.getNessConfig().isDevMode());
		});
	}
	
	/**
	 * Forcibly remove NessPlayer. This may be used to clear the NessPlayer before
	 * automatic expiration of the 10 minute cache.
	 * 
	 * @param player the player to remove
	 */
	void removePlayer(Player player) {
		logger.debug("Removing player {}", player);
		playerCache.invalidate(player.getUniqueId());
	}

	/**
	 * Do something for each NessPlayer
	 * 
	 * @param action what to do
	 */
	public void forEachPlayer(Consumer<NessPlayer> action) {
		playerCache.asMap().values().forEach(action);
	}

}
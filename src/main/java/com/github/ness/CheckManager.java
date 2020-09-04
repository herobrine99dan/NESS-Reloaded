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
		return CompletableFuture.supplyAsync(this::getAllChecks, ness.getExecutor()).whenCompleteAsync((checks, ex) -> {
			if (ex != null) {
				logger.error("Unable to instantiate new checks", ex);
				return;
			}
			processChecks(checks);
		});
	}
	
	private void processChecks(Set<AbstractCheck<?>> checks) {
		logger.debug("Initiating all checks");
		checks.forEach(AbstractCheck::initiate);
		this.checks = checks;
	}

	private Set<AbstractCheck<?>> getAllChecks() {
		Set<AbstractCheck<?>> checks = new HashSet<>();

		configuredCheckLoadLoop:
		for (String checkName : ness.getNessConfig().getEnabledChecks()) {
			if (checkName.equals("AbstractCheck") || checkName.equals("CheckInfo")) {
				logger.warn("Check {} from the config does not exist", checkName);
				continue;
			}
			for (ChecksPackage checkPackage : ChecksPackage.values()) {
				AbstractCheck<?> check = loadCheck(checkPackage.prefix(), checkName);
				if (check != null) {
					checks.add(check);
					continue configuredCheckLoadLoop;
				}
			}
			logger.warn("Check {} does not exist in any package", checkName);
		}
		for (String requiredCheck : ChecksPackage.REQUIRED_CHECKS) {
			AbstractCheck<?> check = loadCheck(".required", requiredCheck);
			if (check == null) {
				logger.error("Required check {} could not be instantiated", requiredCheck);
				continue;
			}
			checks.add(check);
		}
		return checks;
	}
	
	/**
	 * Loads check or returns {@code null} if reflective instantiation failed
	 * 
	 * @param packagePrefix the package prefix in which the check is
	 * @param checkName the check name
	 * @return the instantiated check
	 */
	private AbstractCheck<?> loadCheck(String packagePrefix, String checkName) {
		String clazzName = "com.github.ness.check" + packagePrefix + '.' + checkName;
		try {
			Class<?> clazz = Class.forName(clazzName);
			if (!AbstractCheck.class.isAssignableFrom(clazz)) {
				// This is our fault
				logger.warn("Check exists as {} but does not extend AbstractCheck", clazzName);
				return null;
			}
			Constructor<?> constructor = clazz.getDeclaredConstructor(CheckManager.class);
			return (AbstractCheck<?>) constructor.newInstance(this);

		} catch (ClassNotFoundException ignored) {
			// expected when the check is actually in another package
			logger.trace("Check {} not found in package {}. Other packages will be attempted", checkName, packagePrefix);

		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException ex) {

			// ReflectiveOperationException or other RuntimeException
			// This is likely our fault
			logger.warn("Could not instantiate check {}", clazzName, ex);
		}
		return null;
	}

	CompletableFuture<?> reloadChecks() {
		logger.debug("Reloading all checks");
		checks.forEach(AbstractCheck::close);
		return loadChecks();
	}

	@Override
	public void close() {
		logger.debug("Closing all checks");
		Set<AbstractCheck<?>> checks = this.checks;
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
		logger.debug("Forcibly removing player {}", player);
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
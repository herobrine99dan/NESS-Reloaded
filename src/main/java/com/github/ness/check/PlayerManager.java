package com.github.ness.check;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.ness.NessAnticheat;
import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.api.PlayersManager;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

class PlayerManager implements PlayersManager {

	private final CheckManager checkManager;
	
	private final CoreListener coreListener;
	private final AsyncCache<UUID, NessPlayerData> playerCache;
	
	private static final Logger logger = NessLogger.getLogger(PlayerManager.class);
	
	PlayerManager(CheckManager checkManager) {
		this.checkManager = checkManager;

		coreListener = new CoreListener(this);
		playerCache = Caffeine.newBuilder().removalListener(new PlayerCacheRemovalListener()).buildAsync();
	}
	
	Listener getListener() {
		return coreListener;
	}
	
	AsyncCache<UUID, NessPlayerData> getPlayerCache() {
		return playerCache;
	}
	
	CheckManager getCheckManager() {
		return checkManager;
	}
	
	NessAnticheat ness() {
		return checkManager.getNess();
	}
	
	@Override
	public Collection<NessPlayer> getAllPlayers() {
		Set<NessPlayer> result = new HashSet<>();
		for (NessPlayerData playerData  : playerCache.synchronous().asMap().values()) {
			result.add(playerData.getNessPlayer());
		}
		return Collections.unmodifiableSet(result);
	}
	
	@Override
	public NessPlayer getPlayer(UUID uuid) {
		NessPlayerData playerData = playerCache.synchronous().getIfPresent(uuid);
		return (playerData == null) ? null : playerData.getNessPlayer();
	}
	
	private static class PlayerCacheRemovalListener implements RemovalListener<UUID, NessPlayerData> {

		@Override
		public void onRemoval(@Nullable UUID key, @Nullable NessPlayerData playerData, @NonNull RemovalCause cause) {
			NessPlayer nessPlayer = playerData.getNessPlayer();
			for (Check check : playerData.getChecks()) {
				check.getFactory().removeCheck(nessPlayer);
			}
		}
		
	}
	
	NessPlayer addPlayer(Player player) {
		NessPlayer nessPlayer = new NessPlayer(player, ness().getMainConfig().isDevMode());

		Set<BaseCheckFactory<?>> enabledFactories = new HashSet<>();
		for (BaseCheckFactory<?> factory : checkManager.getCheckFactories()) {
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

	/**
	 * Forcibly remove a player. Used to clear the player's data
	 *
	 * @param player the player to remove
	 */
	void removePlayer(Player player) {
		logger.log(Level.FINE, "Forcibly removing player {0}", player);
		playerCache.synchronous().invalidate(player.getUniqueId());
	}
	
}

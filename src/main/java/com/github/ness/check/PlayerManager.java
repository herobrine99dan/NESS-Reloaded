package com.github.ness.check;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.github.ness.NessAnticheat;
import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.api.PlayersManager;

class PlayerManager implements PlayersManager {

	private final CheckManager checkManager;
	
	private final CoreListener coreListener;
	private final ConcurrentMap<UUID, NessPlayerData> playerCache;
	
	private static final Logger logger = NessLogger.getLogger(PlayerManager.class);
	
	PlayerManager(CheckManager checkManager) {
		this.checkManager = checkManager;

		coreListener = new CoreListener(this);
		playerCache = new ConcurrentHashMap<UUID, NessPlayerData>();
	}
	
	Listener getListener() {
		return coreListener;
	}
	
	ConcurrentMap<UUID, NessPlayerData> getPlayerCache() {
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
		for (NessPlayerData playerData  : playerCache.values()) {
			result.add(playerData.getNessPlayer());
		}
		return Collections.unmodifiableSet(result);
	}
	
	@Override
	public NessPlayer getPlayer(UUID uuid) {
		NessPlayerData playerData = playerCache.get(uuid);
		return (playerData == null) ? null : playerData.getNessPlayer();
	}
	
/*	private static class PlayerCacheRemovalListener implements RemovalListener<UUID, NessPlayerData> {

		@Override
		public void onRemoval(@Nullable UUID key, @Nullable NessPlayerData playerData, @NonNull RemovalCause cause) {
			// playerData cannot be null because GC-based expiration is not used
			NessPlayer nessPlayer = playerData.getNessPlayer();
			for (Check check : playerData.getChecks()) {
				check.getFactory().removeCheck(nessPlayer);
			}
		}
		
	}*/
	
	NessPlayer addPlayer(Player player) {
		NessAnticheat ness = ness();
		NessPlayer nessPlayer = new NessPlayer(player, ness.getMainConfig().isDevMode(), ness);

		Set<BaseCheckFactory<?>> enabledFactories = new HashSet<>();
		for (BaseCheckFactory<?> factory : checkManager.getCheckFactories()) {
			if (!player.hasPermission("ness.bypass." + factory.getCheckName())) {
				enabledFactories.add(factory);
			}
		}
		UUID uuid = player.getUniqueId();
		CompletableFuture<NessPlayerData> future = CompletableFuture.supplyAsync(() -> {

			Set<Check> checks = new HashSet<>(enabledFactories.size());
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
			return value;
		});
		playerCache.put(uuid, future.join());
		/*playerCache.put(uuid, CompletableFuture.supplyAsync(() -> {

			Set<Check> checks = new HashSet<>(enabledFactories.size());
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
			return value;
		}));*/
		return nessPlayer;
	}

	/**
	 * Remove a player. Used to clear the player's data
	 *
	 * @param uuid the uuid of the player to remove
	 */
	void removePlayer(UUID uuid) {
		playerCache.remove(uuid);
	}
	
}

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
import com.github.ness.packets.Packet;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class CheckManager implements ChecksManager {

    private static final Logger logger = NessLogger.getLogger(CheckManager.class);

    private final NessAnticheat ness;
    private final PlayerManager playerManager;

    private BukkitTask kickTask;
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
        kickTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            forEachPlayer(NessPlayer::checkNeedsKick);
        }, 1L, 1L);

        return loadFactories(() -> {
        });
    }

    public CompletableFuture<?> reload() {
        Set<BaseCheckFactory<?>> factories = checkFactories;
        factories.forEach(BaseCheckFactory::close);

        playerManager.getPlayerCache().clear();
        return loadFactories(() -> {
            JavaPlugin plugin = ness.getPlugin();
            ness.getPlugin().getServer().getScheduler().runTask(plugin, () -> {

                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    playerManager.addPlayer(player);
                });
            });
        });
    }

    public void close() {
        kickTask.cancel();
        HandlerList.unregisterAll(playerManager.getListener());
        checkFactories.forEach(BaseCheckFactory::close);
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

        return CompletableFuture.supplyAsync(() -> {
            return new FactoryLoader(this, enabledCheckNames).createAllFactories();
        }, ness.getExecutor()).thenAccept((Set<BaseCheckFactory<?>> checkFactories1) -> {
            checkFactories1.forEach(BaseCheckFactory::start);
            logger.log(Level.FINE, "Started all check factories");
            CheckManager.this.checkFactories = checkFactories1;
            whenComplete.run();
        }).exceptionally((ex) -> {
            logger.log(Level.SEVERE, "Failed to load check factories", ex);
            return null;
        });
    }

    /**
     * Gets a player whose ness player is already loaded. The caller must null
     * check the return value
     *
     * @param player the bukkit player
     * @return the ness player or {@code null} if not loaded
     */
    public NessPlayer getExistingPlayer(Player player) {
        return getExistingPlayer(player.getUniqueId());
    }

    /**
     * Gets a player by UUID whose ness player is already loaded. The caller
     * must null check the return value
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
        playerManager.getPlayerCache().values().forEach((playerData) -> {
            action.accept(playerData.getNessPlayer());
        });
    }

    /**
     * Do something for each check applying to a player. The iteration is
     * consistently ordered
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

    /**
     * Receives a packet and passes it to checks
     *
     * @param packet the packet
     */
    public void receivePacket(Packet packet) {
        for (BaseCheckFactory<?> checkFactory : checkFactories) {
            if (checkFactory instanceof PacketCheckFactory) {
                ((PacketCheckFactory<?>) checkFactory).receivePacket(packet);
            }
        }
    }

}

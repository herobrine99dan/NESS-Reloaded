package com.github.ness;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;

import com.github.ness.check.AbstractCheck;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.Getter;

public class CheckManager implements AutoCloseable {
	
	private final ConcurrentHashMap<UUID, NessPlayer> players = new ConcurrentHashMap<>();
	
	private Set<AbstractCheck<?>> checks;
	
	@Getter
	private final NESSAnticheat ness;
	
	CheckManager(NESSAnticheat ness) {
		this.ness = ness;
	}
	
	CompletableFuture<?> loadAsync() {
		ExecutorService tempExecService = Executors.newWorkStealingPool();
		Future<ScanResult> scanFuture = new ClassGraph().enableClassInfo().scanAsync(tempExecService, Runtime.getRuntime().availableProcessors());

		return CompletableFuture.supplyAsync(this::getAllChecks, ness.getExecutor()).thenApplyAsync((checks) -> {
			this.checks = checks;

			try {
				return scanFuture.get();
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			} finally {
				tempExecService.shutdown();
			}
			return null;
		}).thenAccept((scanResult) -> {
			if (scanResult == null) {
				return;
			}
			EventExecutor eventExecutor = new EventExecutor() {

				@Override
				public void execute(Listener listener, Event evt) throws EventException {
					try {
						if (evt instanceof PlayerJoinEvent) {
							Player player = ((PlayerJoinEvent) evt).getPlayer();
							players.put(player.getUniqueId(), new NessPlayer(player));
						} else if (evt instanceof PlayerQuitEvent) {
							players.remove(((PlayerQuitEvent) evt).getPlayer().getUniqueId()).close();
						} else {
							checks.forEach((check) -> check.checkAnyEvent(evt));
						}
					} catch (Throwable ex) {
						throw new EventException(ex, "NESS made a mistake in listening to an event");
					}
				}

			};
			registerListener(eventExecutor, scanResult);

			checks.forEach((check) -> check.initiatePeriodicTasks());
		});
	}
	
	private void registerListener(EventExecutor eventExecutor, ScanResult scanResult) {
		Listener blankListener = new Listener() {};

		try (ScanResult scan = scanResult) {
			ClassInfoList eventInfos = scan.getClassInfo(Event.class.getName())
			        .getSubclasses().filter(info -> !info.isAbstract());

			for (ClassInfo eventInfo : eventInfos) {

				@SuppressWarnings("unchecked")
				Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(eventInfo.getName());

				boolean found_getHandlers = false;
				boolean found_getHandlerList = false;
				for (Method method : eventClass.getDeclaredMethods()) {
					if (method.getParameterCount() != 0) {
						continue;
					}
					if (!found_getHandlers) {
						found_getHandlers = method.getName().equals("getHandlers");
					}
					if (!found_getHandlerList) {
						found_getHandlerList = method.getName().equals("getHandlerList");
					}
					if (found_getHandlers && found_getHandlerList) {
						break;
					}
				}
				if (found_getHandlers && found_getHandlerList) {
					Bukkit.getPluginManager().registerEvent(eventClass, blankListener, EventPriority.NORMAL, eventExecutor, ness);
				}
			}

		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("This can't happen", ex);

		}
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
					}
				} catch (ClassNotFoundException ex) {

					// No class found!
					// This is the user's fault
					ex.printStackTrace();
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException ex) {

					// Reflection error!
					// This is our fault
					ex.printStackTrace();
				}
			}
		}
		return checks;
	}
	
	@Override
	public void close() {
		checks.forEach(AbstractCheck::close);
		checks.clear();
		HandlerList.unregisterAll(ness);
	}
	
	/**
	 * Gets a NessPlayer or creates one if it does not exist
	 * 
	 * @param player the corresponding player
	 * @return the ness player
	 */
	public NessPlayer getPlayer(Player player) {
		return players.get(player.getUniqueId());
	}
	
	/**
	 * Do something for each NessPlayer
	 * 
	 * @param action what to do
	 */
	public void forEachPlayer(Consumer<NessPlayer> action) {
		players.values().forEach(action);
	}
	
}

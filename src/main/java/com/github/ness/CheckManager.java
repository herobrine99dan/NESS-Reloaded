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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import com.github.ness.check.AbstractCheck;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.Getter;

public class CheckManager implements AutoCloseable {

	private final ConcurrentHashMap<UUID, NessPlayer> players = new ConcurrentHashMap<>();

	private Set<AbstractCheck<?>> checks;

	private static final Logger logger = LogManager.getLogger(CheckManager.class);

	@Getter
	private final NESSAnticheat ness;

	CheckManager(NESSAnticheat ness) {
		this.ness = ness;
	}

	CompletableFuture<?> loadAsync() {
		int parallelism = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
		logger.debug("Using parallelism {} for class scanning", parallelism);
		ExecutorService tempExecService = Executors.newWorkStealingPool(parallelism);
		Future<ScanResult> scanFuture = new ClassGraph().enableClassInfo().scanAsync(tempExecService, parallelism);

		return CompletableFuture.supplyAsync(this::getAllChecks, ness.getExecutor()).thenApplyAsync((checks) -> {
			this.checks = checks;

			try {
				ScanResult result = scanFuture.get();
				return result;
			} catch (InterruptedException | ExecutionException ex) {
				logger.warn("Failed to scan event classes; no events will be listened to", ex);
			} finally {
				tempExecService.shutdown();
			}
			return null;

			// Log messages are more readable using method references
		}).thenApply(this::getEventsToListen).thenAccept(this::processEventClasses);
	}

	private void processEventClasses(Set<Class<? extends Event>> evtClasses) {
		if (evtClasses == null) {
			return;
		}
		logger.trace("All event classes found: {}", evtClasses);
		EventExecutor eventExecutor = new EventExecutor() {
			@Override
			public void execute(Listener listener, Event evt) throws EventException {
				try {
					if (!(evt instanceof PlayerJoinEvent) && !(evt instanceof PlayerQuitEvent)) {
						if (evt instanceof PlayerMoveEvent) {
							PlayerMoveEvent evento = (PlayerMoveEvent) evt;
							if (evento.getTo() == null || evento.getFrom() == null || evento.getPlayer() == null) {
								return;
							}
							if (evento.getTo().getWorld().getName() != evento.getFrom().getWorld().getName()) {
								return;
							}
							MovementValues values = new MovementValues(evento.getPlayer(), evento.getTo(),
									evento.getFrom());
							CheckManager.this.getPlayer(evento.getPlayer()).updateMovementValue(values);
						}
						checks.forEach((check) -> check.checkAnyEvent(evt));
					}
				} catch (Throwable ex) {
					throw new EventException(ex,
							"NESS made a mistake in listening to an event. Please report this error on Github.");
				}
			}
		};
		Listener blankListener = new Listener() {
		};

		Bukkit.getScheduler().runTask(ness, () -> {
			PluginManager pm = Bukkit.getPluginManager();

			// Cleaner
			pm.registerEvents(new Listener() {

				@EventHandler(priority = EventPriority.MONITOR)
				public void onQuit(PlayerQuitEvent evt) {
					Player player = evt.getPlayer();
					logger.debug("Removing player {}", player);

					NessPlayer nessPlayer = players.remove(player.getUniqueId());
					if (nessPlayer != null) {
						nessPlayer.close();
					}
				}

			}, ness);

			// All events
			evtClasses.forEach((eventClass) -> {
				pm.registerEvent(eventClass, blankListener, EventPriority.NORMAL, eventExecutor, ness);
			});
		});
		logger.debug("Registered events, starting periodic tasks");
		checks.forEach((check) -> check.initiatePeriodicTasks());
	}

	/*
	 * Other code that we can use: public void onEnable() { RegisteredListener
	 * registeredListener = new RegisteredListener(this, (listener, event) ->
	 * onEvent(event), EventPriority.NORMAL, this, false); for (HandlerList handler
	 * : HandlerList.getHandlerLists()) { handler.register(registeredListener); } }
	 * 
	 * public Object onEvent(Event event) {
	 * System.out.println(event.getEventName()); return event; }
	 */
	private Set<Class<? extends Event>> getEventsToListen(ScanResult scanResult) {
		if (scanResult == null) {
			return null;
		}
		Set<Class<? extends Event>> result = new HashSet<>();

		try (ScanResult scan = scanResult) {
			ClassInfoList eventInfos = scan.getClassInfo(Event.class.getName()).getSubclasses()
					.filter(info -> !info.isAbstract());

			for (ClassInfo eventInfo : eventInfos) {

				String className = eventInfo.getName();
				if (!className.startsWith("org.bukkit") && !className.startsWith("org.spigotmc")) {
					continue;
				}
				@SuppressWarnings("unchecked")
				Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(className);

				boolean found_getHandlers = false;
				boolean found_getHandlerList = false;
				for (Method method : eventClass.getMethods()) {
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
					logger.trace("Listening to {}", eventClass);
					result.add(eventClass);
				} else {
					logger.trace("Not listening to {}", eventClass);
				}
			}

		} catch (ClassNotFoundException ex) {
			logger.error("ClassNotFoundException on a class we just scanned for ", ex);

		}
		return result;
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

	void reloadChecks() {
		logger.debug("Reloading all checks");
		checks.forEach(AbstractCheck::close);
		// assignation is atomic
		checks = getAllChecks();
		checks.forEach((check) -> check.initiatePeriodicTasks());
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
		return players.computeIfAbsent(player.getUniqueId(), (u) -> {
			logger.debug("Adding player {}", player);
			return new NessPlayer(player, ness.getNessConfig().isDevMode());
		});
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
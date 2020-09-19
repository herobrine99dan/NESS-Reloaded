package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ness.NessLogger;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.HandlerListUtils;
import com.github.ness.utility.UncheckedReflectiveOperationException;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

class ListeningCheckFactory<E extends Event, C extends AbstractCheck<E>> extends CheckFactory<C> {

	private final ScalableRegisteredListener<E> scalableListener;
	private final Class<E> eventClass;
	private final Function<E, UUID> getPlayerFunction;
	
	private static final Logger logger = NessLogger.getLogger(ListeningCheckFactory.class);
	
	ListeningCheckFactory(Constructor<C> constructor, CheckManager manager, CheckInfo<E> checkInfo) {
		super(constructor, manager, checkInfo);
		scalableListener = new ScalableRegisteredListener<>(manager, this);
		eventClass = checkInfo.event;
		getPlayerFunction = findGetPlayerFunction(eventClass);
	}
	
	Class<E> getEventClass() {
		return eventClass;
	}
	
	void checkEvent(E event) {
		logger.log(Level.FINEST, "Listening to event {0}", event);

		if (getPlayerFunction != null) {
			UUID uuid = getPlayerFunction.apply(event);
			logger.log(Level.FINEST, "Found UUID {0} for event", uuid);
			C check = getChecks().get(uuid);
			if (check != null) {
				logger.log(Level.FINEST, "Found check {0} for uuid in event", check);
				check.checkEvent(event);
			}
		} else {
			logger.fine("No UUID involved in event");
			getChecks().values().forEach((check) -> check.checkEvent(event));
		}
		
	}
	
	@Override
	void start0() {
		super.start0();
		HandlerListUtils.getEventListeners(eventClass).register(scalableListener);
	}
	
	@Override
	void close0() {
		super.close0();
		HandlerListUtils.getEventListeners(eventClass).unregister(scalableListener);
	}
	
	private static <E extends Event> Function<E, UUID> findGetPlayerFunction(Class<E> eventClass) {
		if (PlayerEvent.class.isAssignableFrom(eventClass)) {
			return (evt) -> ((PlayerEvent) evt).getPlayer().getUniqueId();
		}
		if (eventClass == ReceivedPacketEvent.class) {
			return (evt) -> ((ReceivedPacketEvent) evt).getNessPlayer().getUUID();
		}
		/*
		 * Bukkit does not implement some sort of "IPlayerEvent" on events with getPlayer
		 * Therefore reflection is necessary
		 */
		try {
			Method getPlayerMethod = eventClass.getMethod("getPlayer");
			return (evt) -> {
				Player player;
				try {
					player = (Player) getPlayerMethod.invoke(evt);
				} catch (IllegalAccessException | InvocationTargetException ex) {
					throw new UncheckedReflectiveOperationException(ex);
				}
				return player.getUniqueId();
			};
		} catch (NoSuchMethodException ignored) {}
		return null;
	}

}

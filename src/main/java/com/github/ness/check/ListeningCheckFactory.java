package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Function;

import com.github.ness.utility.HandlerListUtils;
import com.github.ness.utility.UncheckedReflectiveOperationException;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

class ListeningCheckFactory<E extends Event, C extends AbstractCheck<E>> extends CheckFactory<C> {

	private final ScalableRegisteredListener<E> scalableListener;
	private final Class<E> eventClass;
	private final Function<E, Player> getPlayerFunction;
	
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
		if (getPlayerFunction != null) {
			Player player = getPlayerFunction.apply(event);
			UUID uuid = player.getUniqueId();
			C check = getChecks().get(uuid);
			if (check != null) {
				check.checkEvent(event);
			}
		} else {
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
	
	private static <E extends Event> Function<E, Player> findGetPlayerFunction(Class<E> eventClass) {
		if (PlayerEvent.class.isAssignableFrom(eventClass)) {
			return (evt) -> ((PlayerEvent) evt).getPlayer();
		}
		/*
		 * Bukkit does not implement some sort of "IPlayerEvent" on events with getPlayer
		 * Therefore reflection is necessary
		 */
		try {
			Method getPlayerMethod = eventClass.getMethod("getPlayer");
			return (evt) -> {
				try {
					return (Player) getPlayerMethod.invoke(evt);
				} catch (IllegalAccessException | InvocationTargetException ex) {
					throw new UncheckedReflectiveOperationException(ex);
				}
			};
		} catch (NoSuchMethodException ignored) {}
		return null;
	}

}

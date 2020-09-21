package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.HandlerListUtils;
import com.github.ness.utility.UncheckedReflectiveOperationException;

/**
 * A check factory for {@link ListeningCheck}s
 * 
 * @author A248
 *
 * @param <C> the type of the check
 * @param <E> the type of the event
 */
public class ListeningCheckFactory<C extends ListeningCheck<E>, E extends Event> extends CheckFactory<C> {

	private final ScalableRegisteredListener<E> scalableListener;
	private final Class<E> eventClass;
	private final Function<E, UUID> getPlayerFunction;
	
	ListeningCheckFactory(Constructor<C> constructor, CheckManager manager, ListeningCheckInfo<E> checkInfo) {
		super(constructor, manager, checkInfo);
		scalableListener = new ScalableRegisteredListener<>(manager, this);
		eventClass = checkInfo.getEvent();
		getPlayerFunction = findGetPlayerFunction(eventClass);
	}
	
	protected ListeningCheckFactory(CheckInstantiator<C> instantiator, String checkName, CheckManager manager, ListeningCheckInfo<E> checkInfo) {
		super(instantiator, checkName, manager, checkInfo);
		scalableListener = new ScalableRegisteredListener<>(manager, this);
		eventClass = checkInfo.getEvent();
		getPlayerFunction = findGetPlayerFunction(eventClass);
	}
	
	Class<E> getEventClass() {
		return eventClass;
	}
	
	void checkEvent(E event) {
		if (getPlayerFunction != null) {
			UUID uuid = getPlayerFunction.apply(event);
			C check = getChecksMap().get(uuid);
			if (check != null) {
				check.checkEvent(event);
			}
		} else {
			getChecksMap().values().forEach((check) -> check.checkEvent(event));
		}
	}
	
	@Override
	protected synchronized void start() {
		HandlerListUtils.getEventListeners(eventClass).register(scalableListener);

		super.start();
	}
	
	@Override
	protected synchronized void close() {
		HandlerListUtils.getEventListeners(eventClass).unregister(scalableListener);

		super.close();
	}
	
	private static <E extends Event> Function<E, UUID> findGetPlayerFunction(Class<E> eventClass) {
		if (PlayerEvent.class.isAssignableFrom(eventClass)) {
			return (evt) -> ((PlayerEvent) evt).getPlayer().getUniqueId();
		}
		if (ReceivedPacketEvent.class.isAssignableFrom(eventClass)) {
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

package com.github.ness.check;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
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
			return PlayerEventUUIDFunction.instance();
		}
		if (ReceivedPacketEvent.class.isAssignableFrom(eventClass)) {
			return ReceivedPacketEventUUIDFunction.instance();
		}
		/*
		 * Bukkit does not implement some sort of "IPlayerEvent" on events with getPlayer
		 * Therefore reflection is necessary
		 */
		try {
			Method getPlayerMethod = eventClass.getMethod("getPlayer");
			return new ReflectionGetPlayerUUIDFunction<>(getPlayerMethod);
		} catch (NoSuchMethodException ignored) {}
		return null;
	}
	
	private static class PlayerEventUUIDFunction<E extends Event> implements Function<E, UUID> {

		private static final PlayerEventUUIDFunction<?> INSTANCE = new PlayerEventUUIDFunction<>();
		
		private PlayerEventUUIDFunction() {}
		
		@SuppressWarnings("unchecked")
		static <E extends Event> PlayerEventUUIDFunction<E> instance() {
			return (PlayerEventUUIDFunction<E>) INSTANCE;
		}
		
		@Override
		public UUID apply(E evt) {
			return ((PlayerEvent) evt).getPlayer().getUniqueId();
		}
		
	}
	
	private static class ReceivedPacketEventUUIDFunction<E extends Event> implements Function<E, UUID> {

		private static final ReceivedPacketEventUUIDFunction<?> INSTANCE = new ReceivedPacketEventUUIDFunction<>();
		
		private ReceivedPacketEventUUIDFunction() {}
		
		@SuppressWarnings("unchecked")
		static <E extends Event> ReceivedPacketEventUUIDFunction<E> instance() {
			return (ReceivedPacketEventUUIDFunction<E>) INSTANCE;
		}
		
		@Override
		public UUID apply(E evt) {
			return ((ReceivedPacketEvent) evt).getNessPlayer().getUniqueId();
		}
		
	}
	
	private static class ReflectionGetPlayerUUIDFunction<E extends Event> implements Function<E, UUID> {
		
		private final MethodHandle getPlayerHandle;
		
		ReflectionGetPlayerUUIDFunction(Method getPlayerMethod) {
			try {
				getPlayerHandle = MethodHandles.publicLookup().unreflect(getPlayerMethod);
			} catch (IllegalAccessException ex) {
				throw new UncheckedReflectiveOperationException(ex);
			}
		}

		@Override
		public UUID apply(E evt) {
			Player player;
			try {
				player = (Player) getPlayerHandle.invoke(evt);

			} catch (RuntimeException | Error ex) {
				throw ex;
			} catch (Throwable ex) { // MethodHandle.invoke requires we catch Throwable
				throw new RuntimeException(ex);
			}
			return player.getUniqueId();
		}
		
	}

}

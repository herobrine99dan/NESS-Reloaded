package com.github.ness.check;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

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
	
	protected ListeningCheckFactory(CheckInstantiator<C> instantiator, String checkName,
									CheckManager manager, ListeningCheckInfo<E> checkInfo) {
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
				check.checkEventUnlessInvalid(event);
			}
		} else {
			getChecksMap().values().forEach((check) -> {
				check.checkEventUnlessInvalid(event);
			});
		}
	}
	
	@Override
	protected synchronized void start() {
		new HandlerListSearch<>(eventClass).search().register(scalableListener);

		super.start();
	}
	
	@Override
	protected synchronized void close() {
		new HandlerListSearch<>(eventClass).search().unregister(scalableListener);

		super.close();
	}
	
	private static <E extends Event> Function<E, UUID> findGetPlayerFunction(Class<E> eventClass) {
		if (PlayerEvent.class.isAssignableFrom(eventClass)) {
			return PlayerEventUUIDFunction.instance();
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
	
	private static final class PlayerEventUUIDFunction<E extends Event> implements Function<E, UUID> {

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
	
	private static final class ReflectionGetPlayerUUIDFunction<E extends Event>
			implements Function<E, UUID> {
		
		private final MethodHandle getPlayerHandle;
		
		ReflectionGetPlayerUUIDFunction(Method getPlayerMethod) {
			try {
				getPlayerHandle = MethodHandles.lookup().unreflect(getPlayerMethod);
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

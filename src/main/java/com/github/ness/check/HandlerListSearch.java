package com.github.ness.check;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.ness.utility.UncheckedReflectiveOperationException;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

class HandlerListSearch<E extends Event> {

	private final Class<E> eventClass;

	HandlerListSearch(Class<E> eventClass) {
		this.eventClass = eventClass;
	}

	/**
	 * Gets the handler lists for the event. This is identical to
	 * {@code SimplePluginManager#getEventListeners}
	 *
	 * @return the handler list
	 */
	HandlerList search() {
		try {
			Method getHandlerListMethod = getGetHandlerListMethod(eventClass);
			getHandlerListMethod.setAccessible(true);
			return (HandlerList) getHandlerListMethod.invoke(null);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			throw new UncheckedReflectiveOperationException(ex);
		}
	}

	private static Method getGetHandlerListMethod(Class<? extends Event> clazz) {
		try {
			return clazz.getDeclaredMethod("getHandlerList");

		} catch (NoSuchMethodException ex) {
			Class<?> superClass;
			if ((superClass = clazz.getSuperclass()) != null && superClass != Event.class) {
				return getGetHandlerListMethod(superClass.asSubclass(Event.class));
			} else {
				throw new IllegalStateException("Unable to find getHandlerList for event " + clazz.getName(), ex);
			}
		}
	}

}

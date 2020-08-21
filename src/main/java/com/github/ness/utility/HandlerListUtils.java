package com.github.ness.utility;

import java.lang.reflect.Method;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Utility class for accessing handler lists
 * 
 * @author A248
 *
 */
public class HandlerListUtils {

	/**
	 * Gets the handler lists for an event. This is identical to {@code SimplePluginManager#getEventListeners}
	 * 
	 * @param evtClass the event class
	 * @return the handler list
	 */
    public static HandlerList getEventListeners(Class<? extends Event> evtClass) {
        try {
            Method method = getRegistrationClass(evtClass).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalStateException("Unable to find getHandlerList for event " + clazz.getName(), ex);
            }
        }
    }
	
}

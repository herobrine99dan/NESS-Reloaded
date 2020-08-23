package com.github.ness.utility;

import java.lang.reflect.Method;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

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
    
    /**
     * Empty {@code Listener} for use in registered listeners to maintain compatibility with
     * {@code RegisteredListener}'s undocumented nonnull contracts.
     * 
     * @author A248
     *
     */
    public static final class DummyListener implements Listener {
    	
    	public static final DummyListener INSTANCE = new DummyListener();
    	
    	private DummyListener() {}
    	
    }
    
    /**
     * Empty {@code EventExecutor} for use in registered listeners to maintain compatibility with
     * {@code RegisteredListener}'s undocumented nonnull contracts.
     * 
     * @author A248
     *
     */
    public static final class DummyEventExecutor implements EventExecutor {

    	public static final DummyEventExecutor INSTANCE = new DummyEventExecutor();
    	
    	private DummyEventExecutor() {}
    	
    	/**
    	 * This method should never be called; {@code RegisteredListener}s utilising this dummy
    	 * instance should override the {@code callEvent} method.
    	 * 
    	 * @throws EventException always
    	 */
		@Override
		public void execute(Listener listener, Event event) throws EventException {
			throw new EventException("DummyEventExecutor should never be invoked");
		}
    	
    }
	
}

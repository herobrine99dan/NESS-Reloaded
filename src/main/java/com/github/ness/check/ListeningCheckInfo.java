package com.github.ness.check;

import java.time.Duration;
import java.util.Objects;

import org.bukkit.event.Event;

/**
 * Check info relating to listening of events
 * 
 * @author A248
 *
 * @param <E>
 */
public class ListeningCheckInfo<E extends Event> extends CheckInfo {

    /**
     * Event to listen to
     */
    private final Class<E> event;
    
    ListeningCheckInfo(Class<E> event) {
    	this.event = Objects.requireNonNull(event, "event");
    }
    
    ListeningCheckInfo(Duration asyncInterval, Class<E> event) {
    	super(asyncInterval);
    	this.event = Objects.requireNonNull(event, "event");
    }
    
    Class<E> getEvent() {
    	return event;
    }
	
}

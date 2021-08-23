package com.github.ness.check;

import java.time.Duration;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.github.ness.NessPlayer;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.violation.ViolationManager;

/**
 * A check listening to a certain event
 * 
 * @author A248
 *
 * @param <E> the event type
 */
public abstract class ListeningCheck<E extends Event> extends Check {
	
	protected ListeningCheck(ListeningCheckFactory<?, E> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}
	
	@Override
	public ListeningCheckFactory<?, E> getFactory() {
		@SuppressWarnings("unchecked")
		ListeningCheckFactory<?, E> factory = (ListeningCheckFactory<?, E>) super.getFactory();
		return factory;
	}
	
	/**
	 * Called when this check's event is fired, whether synchronously depends on
	 * whether the event is fired synchronously
	 *
	 * @param evt        the event
	 */
	protected abstract void checkEvent(E evt);

	void checkEventUnlessInvalid(E event) {
		if (player().isInvalid()) {
			return;
		}
		checkEvent(event);
	}
	
	/**
	 * Utility method to get material name with MaterialAccess implementation
         * @param immutableLoc
         * @return String
         * 
	 */
	
	public String getMaterialName(ImmutableLoc loc) {
		return this.getMaterialAccess().getMaterial(loc.toBukkitLocation()).name();
	}

}

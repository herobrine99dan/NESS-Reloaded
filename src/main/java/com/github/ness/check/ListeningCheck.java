package com.github.ness.check;

import java.time.Duration;

import com.github.ness.NessPlayer;
import com.github.ness.config.NessConfig;
import com.github.ness.violation.ViolationTriggerSection.CancelEvent;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

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
	 * Whether calls to {@link #flagEvent(Cancellable)} and {@link #flagEvent(Cancellable, String)} should
	 * drag the player down if they cancel the event. False by default but may be overridden
	 * 
	 * @return true to drag down, false otherwise
	 */
	protected boolean shouldDragDown() {
		return false;
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
	 * Flags the player for cheating, and cancels the event if the violation count is too high (when configured)
	 * 
	 * @param evt the event to cancel
	 */
	protected final void flagEvent(Cancellable evt) {
		flagEvent(evt, "");
	}
	
	/**
	 * Flags the player for cheating, and cancels the event if the violation count is too high (when configured)
	 * 
	 * @param evt the event to cancel
	 * @param details debugging details
	 */
	protected final void flagEvent(Cancellable evt, String details) {
		if (callFlagEvent()) {
			int violations = flag0(details).getCount();
			NessConfig config = getFactory().getCheckManager().getNess().getMainConfig();
			CancelEvent cancelEvent = config.getViolationHandling().cancelEvent();
			if (cancelEvent.enable() && violations >= cancelEvent.violations()) {
				evt.setCancelled(true);
				if (shouldDragDown()) {
					runTaskLater(player()::completeDragDown, Duration.ZERO);
				}
			}
		}
	}

}

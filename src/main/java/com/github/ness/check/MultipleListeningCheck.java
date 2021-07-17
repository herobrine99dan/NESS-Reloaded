package com.github.ness.check;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.github.ness.NessPlayer;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.violation.ViolationManager;

public abstract class MultipleListeningCheck extends Check{
	
	protected MultipleListeningCheck(MultipleListeningCheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}
	
	@Override
	public MultipleListeningCheckFactory<?> getFactory() {
		@SuppressWarnings("unchecked")
		MultipleListeningCheckFactory<?> factory = (MultipleListeningCheckFactory<?>) super.getFactory();
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
	protected abstract void checkEvent(Event evt);

	void checkEventUnlessInvalid(Event event) {
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
	 * Utility method to get material name with MaterialAccess implementation
	 */
	
	public String getMaterialName(ImmutableLoc loc) {
		return this.getMaterialAccess().getMaterial(loc.toBukkitLocation()).name();
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
			ViolationManager violationManager = getFactory().getCheckManager().getNess().getViolationManager();
			//TODO Implement later
			/*SetBack setBackToUse = violationManager.shouldCancelWithSetBack(this, violations);
			if (setBackToUse != null) {
				if (setBackToUse.shouldRunOnDelay()) {
					NessPlayer player = player();
					runTaskLater(() -> setBackToUse.doSetBack(player, evt), Duration.ZERO);
				} else {
					setBackToUse.doSetBack(player(), evt);
				}
			}*/
		}
	}

}

package com.github.ness.check;

import java.time.Duration;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import com.github.ness.NessPlayer;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.violation.ViolationManager;

public abstract class MultipleListeningCheck extends Check {

	protected MultipleListeningCheck(MultipleListeningCheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}

	@Override
	public MultipleListeningCheckFactory<?> getFactory() {
		MultipleListeningCheckFactory<?> factory = (MultipleListeningCheckFactory<?>) super.getFactory();
		return factory;
	}

	/**
	 * Whether calls to {@link #flagEvent(Cancellable)} and
	 * {@link #flagEvent(Cancellable, String)} should drag the player down if they
	 * cancel the event. False by default but may be overridden
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
	 * @param evt the event
	 */
	protected abstract void checkEvent(Event evt);

	void checkEventUnlessInvalid(Event event) {
		if (player().isInvalid()) {
			return;
		}
		if (event instanceof PlayerEvent && player().isNot(((PlayerEvent) event).getPlayer())) {
                        return;
                }
		checkEvent(event);
	}

	/**
	 * Utility method to get material name with MaterialAccess implementation
         * @param ImmutableLoc loc
         * @return String
	 */

	public String getMaterialName(ImmutableLoc loc) {
		return this.getMaterialAccess().getMaterial(loc.toBukkitLocation()).name();
	}

}

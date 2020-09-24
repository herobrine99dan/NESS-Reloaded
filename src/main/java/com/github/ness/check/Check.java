package com.github.ness.check;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.ness.NessPlayer;
import com.github.ness.api.Infraction;

/**
 * A check, associated with a player. Includes an optional async task.
 * 
 * @author A248
 *
 */
public class Check extends BaseCheck {
	
	private final NessPlayer nessPlayer;
	
	private final AtomicInteger violations = new AtomicInteger();
	
	protected Check(CheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory);
		this.nessPlayer = nessPlayer;
	}
	
	@Override
	protected CheckFactory<?> getFactory() {
		return (CheckFactory<?>) super.getFactory();
	}
	
	/**
	 * Gets the player this check is for
	 * 
	 * @return the player analysed by this check
	 */
	protected NessPlayer player() {
		return nessPlayer;
	}
	
	/**
	 * Called async and periodically, if defined by {@link CheckInfo}
	 *
	 */
	protected void checkAsyncPeriodic() {
		throw new UnsupportedOperationException("Not implemented - checkAsyncPeriodic");
	}
	
	/**
	 * Flags the player for cheating
	 * 
	 */
	protected void flag() {
		flag0();
	}
	
	int flag0() {
		int violations = this.violations.getAndIncrement();
		nessPlayer.getInfractions().add(Infraction.of(getFactory(), violations));
		return violations;
	}

}

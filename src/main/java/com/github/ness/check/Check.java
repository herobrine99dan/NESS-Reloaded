package com.github.ness.check;

import com.github.ness.NessPlayer;

/**
 * A check, associated with a player. Includes an optional async task.
 * 
 * @author A248
 *
 */
public class Check extends BaseCheck {
	
	private final NessPlayer nessPlayer;
	
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

}

package com.github.ness.check;

import com.github.ness.NessPlayer;

/**
 * Base class for
 * 
 * @author A248
 *
 */
public abstract class BaseCheckFactory<C extends BaseCheck> {

	private final String checkName;
	private final CheckManager manager;
	private final BaseCheckInfo checkInfo;
	
	private transient boolean started;
	
	protected BaseCheckFactory(String checkName, CheckManager manager, BaseCheckInfo checkInfo) {
		this.checkName = checkName;
		this.manager = manager;
		this.checkInfo = checkInfo;
	}
	
	CheckManager getCheckManager() {
		return manager;
	}
	
	String getCheckName() {
		return checkName;
	}
	
	BaseCheckInfo getCheckInfo() {
		return checkInfo;
	}
	
	abstract C newCheck(NessPlayer nessPlayer);
	
	abstract void removeCheck(NessPlayer nessPlayer);
	
	synchronized boolean started() {
		return started;
	}
	
	/**
	 * Called to start the check factory. Subclasses should always call {@code super.start} as the last statement
	 * in their implementation if they override this method. <br>
	 * <br>
	 * This should never be called except by the check framework. At any time, a check factory is either
	 * starting (this method), shutting down ({@link #close()}), or neither.
	 * 
	 */
	protected synchronized void start() {
		started = true;
	}
	
	/**
	 * Called to close the check factory. Subclasses should always call {@code super.close} as the last statement
	 * in their implementation if they override this method. <br>
	 * <br>
	 * This should never be called except by the check framework. At any time, a check factory is either
	 * starting({@link #start()}), shutting down (this method), or neither.
	 * 
	 */
	protected synchronized void close() {
		started = false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [checkName=" + checkName + ", manager=" + manager + ", checkInfo=" + checkInfo + "]";
	}
	
}
